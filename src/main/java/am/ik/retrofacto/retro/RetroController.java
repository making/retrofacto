package am.ik.retrofacto.retro;

import am.ik.retrofacto.retro.RetroService.UpdateCard;
import am.ik.retrofacto.retro.event.CardCreateEvent;
import am.ik.retrofacto.retro.event.CardDeleteEvent;
import am.ik.retrofacto.retro.event.CardLoadEvent;
import am.ik.retrofacto.retro.event.CardUpdateEvent;
import am.ik.retrofacto.retro.sse.SseEmitterManager;
import io.hypersistence.tsid.TSID;

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

	private final SseEmitterManager sseEmitterManager;

	public RetroController(RetroService retroService, BoardRepository boardRepository, CardRepository cardRepository,
			SseEmitterManager sseEmitterManager) {
		this.retroService = retroService;
		this.boardRepository = boardRepository;
		this.cardRepository = cardRepository;
		this.sseEmitterManager = sseEmitterManager;
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
		SseEmitter emitter = this.sseEmitterManager.newEmitter(slug);
		this.boardRepository.findBySlug(slug)
			.ifPresent(board -> this.sseEmitterManager.sendEvent(emitter, new CardLoadEvent(board)));
		return emitter;
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
		this.sseEmitterManager.broadcastEvent(slug, new CardCreateEvent(card));
		return ResponseEntity.created(builder.path("/cards/{cardId}").build(created.getId())).body(created);
	}

	@PatchMapping(path = "/boards/{slug}/cards/{cardId}")
	public ResponseEntity<Void> updateCard(@PathVariable String slug, @PathVariable TSID cardId,
			@RequestBody UpdateCard update) {
		this.retroService.updateCard(cardId, update)
			.ifPresent(card -> this.sseEmitterManager.broadcastEvent(slug, new CardUpdateEvent(card)));
		return ResponseEntity.accepted().build();
	}

	@PostMapping(path = "/boards/{slug}/cards/{cardId}/like")
	public ResponseEntity<Void> addLike(@PathVariable String slug, @PathVariable TSID cardId) {
		this.retroService.addLike(cardId)
			.ifPresent(card -> this.sseEmitterManager.broadcastEvent(slug, new CardUpdateEvent(card)));
		return ResponseEntity.accepted().build();
	}

	@DeleteMapping(path = "/boards/{slug}/cards/{cardId}")
	public ResponseEntity<Void> deleteCard(@PathVariable String slug, @PathVariable TSID cardId) {
		this.retroService.deleteCard(cardId)
			.ifPresent(card -> this.sseEmitterManager.broadcastEvent(slug,
					new CardDeleteEvent(cardId, card.getColumn().getId())));
		return ResponseEntity.accepted().build();
	}

	public record CreateBoardRequest(String name, String slug, String passphrase) {

	}

	public record CreateCardRequest(String text, TSID columnId) {

	}

}
