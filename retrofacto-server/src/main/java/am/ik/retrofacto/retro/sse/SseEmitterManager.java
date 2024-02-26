package am.ik.retrofacto.retro.sse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import am.ik.retrofacto.retro.event.CardEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager implements EventHandler {

	private final JdbcClient jdbcClient;

	private final ObjectMapper objectMapper;

	private final ConcurrentMap<String, ConcurrentMap<UUID, SseEmitter>> emittersMap = new ConcurrentHashMap<>();

	private final Logger log = LoggerFactory.getLogger(SseEmitterManager.class);

	private final MeterRegistry meterRegistry;

	public SseEmitterManager(JdbcClient jdbcClient, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
		this.jdbcClient = jdbcClient;
		this.objectMapper = objectMapper;
		this.meterRegistry = meterRegistry;
	}

	public Map.Entry<UUID, SseEmitter> newEmitter(String slug) {
		SseEmitter emitter = new SseEmitter(TimeUnit.HOURS.toMillis(2));
		UUID emitterId = UUID.randomUUID();
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.computeIfAbsent(slug, k -> {
			var map = new ConcurrentHashMap<UUID, SseEmitter>();
			FunctionCounter.builder("retro_subscribers", map, ConcurrentHashMap::size)
				.description("the number of retro subscribers")
				.tag("slug", slug)
				.register(this.meterRegistry);
			return map;
		});
		emitters.put(emitterId, emitter);
		emitter.onCompletion(() -> {
			log.debug("Complete on emitter ({})", emitterId);
			emitters.remove(emitterId);
		});
		emitter.onTimeout(() -> {
			log.debug("Timeout on emitter ({})", emitterId);
			emitters.remove(emitterId);
		});
		emitter.onError(e -> {
			log.debug("Exception occurred on emitter (%s)".formatted(emitterId), e);
			emitters.remove(emitterId);
		});
		return Map.entry(emitterId, emitter);
	}

	public void sendImmediateEvent(String slug, UUID emitterId, CardEvent cardEvent) {
		try {
			ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.get(slug);
			if (emitters != null) {
				SseEmitter emitter = emitters.get(emitterId);
				if (emitter != null) {
					emitter.send(cardEvent);
				}
			}
		}
		catch (IOException e) {
			log.info("Failed to send event on emitter", e);
		}
	}

	@Transactional
	public void broadcastEvent(String slug, UUID senderId, CardEvent cardEvent) {
		try {
			String payload = this.objectMapper.writeValueAsString(cardEvent);
			this.jdbcClient.sql("""
					SELECT pg_notify('retrofacto_event', :message)
					""").param("message", "%s,%s,%s".formatted(slug, senderId, payload)).query().singleRow();
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void onEvent(String slug, UUID senderId, String payload) {
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.get(slug);
		if (emitters != null) {
			for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
				UUID emitterId = entry.getKey();
				SseEmitter emitter = entry.getValue();
				try {
					emitter.send(payload);
				}
				catch (IOException e) {
					log.debug("Failed to send event on emitter (%s)".formatted(emitterId), e);
					emitter.completeWithError(e);
				}
				catch (IllegalStateException e) {
					log.warn("Failed to send event on emitter (%s)".formatted(emitterId), e);
					emitter.completeWithError(e);
				}
			}
		}
	}

}
