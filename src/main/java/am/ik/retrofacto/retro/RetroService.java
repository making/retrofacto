package am.ik.retrofacto.retro;

import java.util.List;
import java.util.Optional;

import io.hypersistence.tsid.TSID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static am.ik.retrofacto.retro.BoardBuilder.board;
import static am.ik.retrofacto.retro.ColumnBuilder.column;

@Service
public class RetroService {

	private final BoardRepository boardRepository;

	private final CardRepository cardRepository;

	private final PasswordEncoder passwordEncoder;

	public RetroService(BoardRepository boardRepository, CardRepository cardRepository,
			PasswordEncoder passwordEncoder) {
		this.boardRepository = boardRepository;
		this.cardRepository = cardRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Optional<Board> findBySlug(String slug) {
		return this.boardRepository.findBySlug(slug);
	}

	public boolean checkPassword(String rawPassword, Board board) {
		if (!StringUtils.hasText(board.getPassphrase())) {
			return true;
		}
		return this.passwordEncoder.matches(rawPassword, board.getPassphrase());
	}

	@Transactional
	public Board createBoard(String name, String slug, String passphrase) {
		// TODO check if the given slug already exits
		Column green = column().title("I'm glad that...").emoji("😀").color("#70beb1").cards(List.of()).build();
		Column yellow = column().title("I'm wondering about...").emoji("🤔").color("#f5c94c").cards(List.of()).build();
		Column red = column().title("It wasn't so great that...").emoji("😱").color("#d35948").cards(List.of()).build();
		Board board = board().name(name)
			.slug(slug)
			.passphrase(StringUtils.hasText(passphrase) ? this.passwordEncoder.encode(passphrase) : null)
			.columns(List.of(green, yellow, red))
			.build();
		green.setBoard(board);
		yellow.setBoard(board);
		red.setBoard(board);
		return this.boardRepository.save(board);
	}

	@Transactional
	public Optional<Card> updateCard(TSID cardId, UpdateCard updateCard) {
		return this.cardRepository.findById(cardId).map(card -> {
			if (StringUtils.hasText(updateCard.text())) {
				card.setText(updateCard.text());
			}
			if (updateCard.done() != null) {
				card.setDone(updateCard.done());
			}
			if (updateCard.columnId() != null) {
				card.setColumn(new Column(updateCard.columnId));
			}
			return card;
		});
	}

	@Transactional
	public Optional<Board> deleteBoard(String slug) {
		return this.boardRepository.findBySlug(slug).map(board -> {
			this.boardRepository.delete(board);
			return board;
		});
	}

	@Transactional
	public Optional<Card> addLike(TSID cardId) {
		this.cardRepository.addLike(cardId);
		return this.cardRepository.findById(cardId);
	}

	@Transactional
	public Optional<Card> deleteCard(TSID cardId) {
		return this.cardRepository.findById(cardId).map(card -> {
			this.cardRepository.delete(card);
			return card;
		});
	}

	public record UpdateCard(String text, Boolean done, TSID columnId) {

	}

}
