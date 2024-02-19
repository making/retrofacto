package am.ik.retrofacto.retro.event;

import java.util.List;

import am.ik.retrofacto.retro.Board;
import am.ik.retrofacto.retro.Card;

public record CardLoadEvent(Board board) implements CardEvent {

	@Override
	public Type type() {
		return Type.LOAD;
	}
}
