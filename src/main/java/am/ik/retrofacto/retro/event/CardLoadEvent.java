package am.ik.retrofacto.retro.event;

import java.util.UUID;

import am.ik.retrofacto.retro.Board;

public record CardLoadEvent(UUID emitterId, Board board) implements CardEvent {

	@Override
	public Type type() {
		return Type.LOAD;
	}
}
