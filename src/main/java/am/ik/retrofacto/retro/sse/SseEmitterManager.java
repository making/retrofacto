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

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager implements MessageHandler {

	private final MessageChannel retrofactoChannel;

	private final ObjectMapper objectMapper;

	private final ConcurrentMap<String, ConcurrentMap<UUID, SseEmitter>> emittersMap = new ConcurrentHashMap<>();

	private final Logger log = LoggerFactory.getLogger(SseEmitterManager.class);

	private final MeterRegistry meterRegistry;

	public SseEmitterManager(MessageChannel retrofactoChannel, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
		this.retrofactoChannel = retrofactoChannel;
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

	public void broadcastEvent(String slug, UUID senderId, CardEvent cardEvent) {
		this.sendImmediateEvent(slug, senderId, cardEvent);
		try {
			String payload = this.objectMapper.writeValueAsString(cardEvent);
			Message<String> message = MessageBuilder.withPayload(payload)
				.setHeader("slug", slug)
				.setHeader("sender", Objects.requireNonNull(senderId.toString()))
				.build();
			this.retrofactoChannel.send(message);
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		String slug = Objects.requireNonNull(message.getHeaders().get("slug")).toString();
		UUID senderId = UUID.fromString(Objects.requireNonNull(message.getHeaders().get("sender")).toString());
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.get(slug);
		if (emitters != null) {
			for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
				UUID emitterId = entry.getKey();
				if (Objects.equals(emitterId, senderId)) {
					continue;
				}
				try {
					SseEmitter emitter = entry.getValue();
					emitter.send(message.getPayload());
				}
				catch (IOException e) {
					log.debug("Failed to send event on emitter (%s)".formatted(emitterId), e);
				}
			}
		}
	}

}
