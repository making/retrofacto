package am.ik.retrofacto.retro;

import java.util.Optional;

import io.hypersistence.tsid.TSID;

import org.springframework.data.repository.ListCrudRepository;

public interface BoardRepository extends ListCrudRepository<Board, TSID> {

	Optional<Board> findBySlug(String slug);

}
