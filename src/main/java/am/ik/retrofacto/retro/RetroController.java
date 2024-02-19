package am.ik.retrofacto.retro;

import am.ik.retrofacto.retro.RetroService.UpdateCard;
import io.hypersistence.tsid.TSID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@CrossOrigin
public class RetroController {

	private final RetroService retroService;

	private final BoardRepository boardRepository;

	private final CardRepository cardRepository;

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

	@PostMapping(path = "/cards")
	public ResponseEntity<Card> createCard(@RequestBody CreateCardRequest request, UriComponentsBuilder builder) {
		Card card = CardBuilder.card()
			.text(request.text())
			.done(false)
			.like(0)
			.column(new Column(request.columnId()))
			.build();
		Card created = this.cardRepository.save(card);
		return ResponseEntity.created(builder.path("/cards/{cardId}").build(created.getId())).body(created);
	}

	@PatchMapping(path = "/cards/{cardId}")
	public ResponseEntity<Void> updateCard(@PathVariable TSID cardId, @RequestBody UpdateCard update) {
		this.retroService.updateCard(cardId, update);
		return ResponseEntity.accepted().build();
	}

	@PostMapping(path = "/cards/{cardId}/like")
	public ResponseEntity<Void> addLike(@PathVariable TSID cardId) {
		this.cardRepository.addLike(cardId);
		return ResponseEntity.accepted().build();
	}

	@DeleteMapping(path = "/cards/{cardId}")
	public ResponseEntity<Void> deleteCard(@PathVariable TSID cardId) {
		this.cardRepository.deleteById(cardId);
		return ResponseEntity.accepted().build();
	}

	public record CreateBoardRequest(String name, String slug, String passphrase) {

	}

	public record CreateCardRequest(String text, TSID columnId) {

	}

}
