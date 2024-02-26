package am.ik.retrofacto.retro;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.tsid.TSID;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.jilt.Builder;
import org.jilt.BuilderStyle;
import org.jilt.Opt;

@Entity
@Table(name = "columns")
@DynamicUpdate
public class Column implements Serializable {

	@Id
	@Tsid
	private TSID id;

	@jakarta.persistence.Column(nullable = false)
	private String title;

	@jakarta.persistence.Column(nullable = false)
	private String emoji;

	@jakarta.persistence.Column(nullable = false)
	private String color;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "column", orphanRemoval = true)
	private List<Card> cards;

	@ManyToOne
	@JoinColumn(nullable = false)
	@JsonIgnore
	private Board board;

	public Column() {
	}

	public Column(TSID id) {
		this.id = id;
	}

	@Builder(style = BuilderStyle.STAGED)
	public Column(@Opt TSID id, String title, String emoji, String color, @Opt List<Card> cards) {
		this.id = id;
		this.title = title;
		this.emoji = emoji;
		this.color = color;
		this.cards = cards;
	}

	public TSID getId() {
		return id;
	}

	public void setId(TSID id) {
		this.id = id;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmoji() {
		return emoji;
	}

	public void setEmoji(String emoji) {
		this.emoji = emoji;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	@Override
	public String toString() {
		return "Column{" + "id=" + id + ", title='" + title + '\'' + '}';
	}

}
