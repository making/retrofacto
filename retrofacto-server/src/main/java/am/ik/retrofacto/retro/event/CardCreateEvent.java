package am.ik.retrofacto.retro.event;

import am.ik.retrofacto.retro.Card;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hypersistence.tsid.TSID;

public record CardCreateEvent(Card card) implements CardEvent {
	@Override
	public Type type() {
		return Type.CREATE;
	}

	@JsonProperty
	public TSID columnId() {
		return this.card.getColumn().getId();
	}
}
