package am.ik.retrofacto.retro;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import am.ik.retrofacto.retro.RetroService.UpdateCard;
import am.ik.retrofacto.retro.event.CardCreateEvent;
import am.ik.retrofacto.retro.event.CardDeleteEvent;
import am.ik.retrofacto.retro.event.CardEvent;
import am.ik.retrofacto.retro.event.CardLoadEvent;
import am.ik.retrofacto.retro.event.CardUpdateEvent;
import io.hypersistence.tsid.TSID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@CrossOrigin
public class RetroController {

	private final RetroService retroService;

	private final BoardRepository boardRepository;

	private final CardRepository cardRepository;

	private final ConcurrentMap<String, ConcurrentMap<UUID, SseEmitter>> emittersMap = new ConcurrentHashMap<>();

	private final Logger log = LoggerFactory.getLogger(RetroController.class);

	public RetroController(RetroService retroService, BoardRepository boardRepository, CardRepository cardRepository) {
		this.retroService = retroService;
		this.boardRepository = boardRepository;
		this.cardRepository = cardRepository;
	}

	@PostMapping(path = "/boards")
	public ResponseEntity<Board> createBoard(@RequestBody CreateBoardRequest request, UriComponentsBuilder builder) {
		Board board = this.retroService.createBoard(request.name(), request.slug(), request.passphrase());
		return ResponseEntity.created(builder.path("/boards/{slug}").build(board.getSlug())).body(board);
	}

	@GetMapping(path = "/boards/{slug}")
	public ResponseEntity<Board> getBoard(@PathVariable String slug) {
		return ResponseEntity.of(this.boardRepository.findBySlug(slug));
	}

	@GetMapping(path = "/boards/{slug}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter cardEvents(@PathVariable String slug) {
		SseEmitter emitter = new SseEmitter(TimeUnit.HOURS.toMillis(2));
		UUID emitterId = UUID.randomUUID();
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.computeIfAbsent(slug,
				k -> new ConcurrentHashMap<>());
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
		this.boardRepository.findBySlug(slug).ifPresent(board -> {
			try {
				emitter.send(new CardLoadEvent(board));
			}
			catch (IOException e) {
				log.info("Failed to send event on emitter (%s)".formatted(emitterId), e);
			}
		});
		return emitter;
	}

	private void broadcastEvent(String slug, CardEvent cardEvent) {
		ConcurrentMap<UUID, SseEmitter> emitters = this.emittersMap.get(slug);
		for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
			try {
				entry.getValue().send(cardEvent);
			}
			catch (IOException e) {
				log.debug("Failed to send event on emitter (%s)".formatted(entry.getKey()), e);
			}
		}
	}

	@PostMapping(path = "/boards/{slug}/cards")
	public ResponseEntity<Card> createCard(@PathVariable String slug, @RequestBody CreateCardRequest request,
			UriComponentsBuilder builder) {
		Card card = CardBuilder.card()
			.text(request.text())
			.done(false)
			.like(0)
			.column(new Column(request.columnId()))
			.build();
		Card created = this.cardRepository.save(card);
		this.broadcastEvent(slug, new CardCreateEvent(card));
		return ResponseEntity.created(builder.path("/cards/{cardId}").build(created.getId())).body(created);
	}

	@PatchMapping(path = "/boards/{slug}/cards/{cardId}")
	public ResponseEntity<Void> updateCard(@PathVariable String slug, @PathVariable TSID cardId,
			@RequestBody UpdateCard update) {
		this.retroService.updateCard(cardId, update)
			.ifPresent(card -> this.broadcastEvent(slug, new CardUpdateEvent(card)));
		return ResponseEntity.accepted().build();
	}

	@PostMapping(path = "/boards/{slug}/cards/{cardId}/like")
	public ResponseEntity<Void> addLike(@PathVariable String slug, @PathVariable TSID cardId) {
		this.retroService.addLike(cardId).ifPresent(card -> this.broadcastEvent(slug, new CardUpdateEvent(card)));
		return ResponseEntity.accepted().build();
	}

	@DeleteMapping(path = "/boards/{slug}/cards/{cardId}")
	public ResponseEntity<Void> deleteCard(@PathVariable String slug, @PathVariable TSID cardId) {
		this.retroService.deleteCard(cardId)
			.ifPresent(card -> this.broadcastEvent(slug, new CardDeleteEvent(cardId, card.getColumn().getId())));
		return ResponseEntity.accepted().build();
	}

	public record CreateBoardRequest(String name, String slug, String passphrase) {

	}

	public record CreateCardRequest(String text, TSID columnId) {

	}

}
