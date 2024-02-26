package am.ik.retrofacto.retro.event;

import io.hypersistence.tsid.TSID;

public record CardDeleteEvent(TSID cardId, TSID columnId) implements CardEvent {
	@Override
	public Type type() {
		return Type.DELETE;
	}
}
