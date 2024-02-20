package am.ik.retrofacto.retro.sse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import am.ik.retrofacto.retro.event.CardEvent;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterManager {

	private final ConcurrentMap<String, ConcurrentMap<UUID, SseEmitter>> emittersMap = new ConcurrentHashMap<>();

	private final Logger log = LoggerFactory.getLogger(SseEmitterManager.class);

	private final MeterRegistry meterRegistry;

	public SseEmitterManager(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	public SseEmitter newEmitter(String slug) {
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
		return emitter;
	}

	public void sendEvent(SseEmitter emitter, CardEvent cardEvent) {
		try {
			emitter.send(cardEvent);
		}
		catch (IOException e) {
			log.info("Failed to send event on emitter", e);
		}
	}

	public void broadcastEvent(String slug, CardEvent cardEvent) {
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.get(slug);
		for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
			try {
				SseEmitter emitter = entry.getValue();
				emitter.send(cardEvent);
			}
			catch (IOException e) {
				UUID emitterId = entry.getKey();
				log.debug("Failed to send event on emitter (%s)".formatted(emitterId), e);
			}
		}
	}

}
