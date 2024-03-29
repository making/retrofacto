package am.ik.retrofacto.retro;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.tsid.TSID;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.DynamicUpdate;
import org.jilt.Builder;
import org.jilt.BuilderStyle;
import org.jilt.Opt;

@Entity
@Table(name = "cards")
@DynamicUpdate
public class Card implements Serializable {

	@Id
	@Tsid
	@Column(nullable = false)
	private TSID id;

	@Column(nullable = false)
	private String text = "";

	@Column(nullable = false)
	private boolean done;

	@Column(name = "likes", nullable = false)
	private int like;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@JsonIgnore
	private am.ik.retrofacto.retro.Column column;

	@Version
	@Column(nullable = false)
	@JsonIgnore
	private int version;

	@SuppressWarnings("NullAway")
	public Card() {
	}

	@Builder(style = BuilderStyle.STAGED)
	public Card(@Opt TSID id, String text, boolean done, int like, am.ik.retrofacto.retro.Column column) {
		this.id = id;
		this.text = text;
		this.done = done;
		this.like = like;
		this.column = column;
	}

	public void setId(TSID id) {
		this.id = id;
	}

	public TSID getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public am.ik.retrofacto.retro.Column getColumn() {
		return column;
	}

	public void setColumn(am.ik.retrofacto.retro.Column column) {
		this.column = column;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Card{" + "id=" + id + ", text='" + text + '\'' + ", done=" + done + ", like=" + like + ", column="
				+ column + ", version=" + version + '}';
	}

}
