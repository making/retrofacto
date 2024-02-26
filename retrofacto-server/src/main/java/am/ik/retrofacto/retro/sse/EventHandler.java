package am.ik.retrofacto.retro.sse;

import java.util.UUID;

@FunctionalInterface
public interface EventHandler {

	void onEvent(String slug, String payload);

}
