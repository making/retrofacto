package am.ik.retrofacto.retro.sse;

import io.hypersistence.tsid.TSID;

public record NotifiedEvent(TSID tsid, String slug, String payload) implements Comparable<NotifiedEvent> {
	public static NotifiedEvent valueOf(String s) {
		String[] vals = s.split(",", 3);
		return new NotifiedEvent(TSID.from(vals[0]), vals[1], vals[2]);
	}

	@Override
	public int compareTo(NotifiedEvent o) {
		return this.tsid.compareTo(o.tsid);
	}
}
