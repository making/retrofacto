package am.ik.retrofacto.retro.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface CardEvent permits CardCreateEvent, CardDeleteEvent, CardLoadEvent, CardUpdateEvent {

	@JsonProperty
	Type type();

	enum Type {

		LOAD, CREATE, DELETE, UPDATE

	}

}
