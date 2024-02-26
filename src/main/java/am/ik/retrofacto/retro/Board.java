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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.jilt.Builder;
import org.jilt.BuilderStyle;
import org.jilt.Opt;

@Entity
@Table(name = "boards")
@DynamicUpdate
public class Board implements Serializable {

	@Id
	@Tsid
	private TSID id;

	@jakarta.persistence.Column(nullable = false)
	private String name;

	@jakarta.persistence.Column(nullable = false, unique = true)
	private String slug;

	@jakarta.persistence.Column(nullable = true)
	@JsonIgnore
	private String passphrase;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "board")
	private List<Column> columns;

	public Board() {
	}

	public Board(TSID id) {
		this.id = id;
	}

	@Builder(style = BuilderStyle.STAGED)
	public Board(@Opt TSID id, String name, String slug, String passphrase, List<Column> columns) {
		this.id = id;
		this.name = name;
		this.slug = slug;
		this.passphrase = passphrase;
		this.columns = columns;
	}

	public TSID getId() {
		return id;
	}

	public void setId(TSID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "Board{" + "id=" + id + ", name='" + name + '\'' + ", slug='" + slug + '\'' + '}';
	}

}
