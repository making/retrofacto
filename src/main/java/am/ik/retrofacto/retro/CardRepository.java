package am.ik.retrofacto.retro;

import io.hypersistence.tsid.TSID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CardRepository extends ListCrudRepository<Card, TSID> {

	@Query(value = "UPDATE Card c SET c.like = c.like + 1 WHERE c.id = :cardId")
	@Modifying
	@Transactional
	int addLike(TSID cardId);

}
