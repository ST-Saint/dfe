package edu.purdue.dfe.data;

public class ProbeData {

	private final long id;

	private final String name;

	private final boolean[] probes;

	public ProbeData(final long id, final String name, final boolean[] probes) {
		this.id = id;
		this.name = name;
		this.probes = probes;
	}

	public ProbeData(final long id, final String name, final int probeCount) {
		this.id = id;
		this.name = name;
		this.probes = new boolean[probeCount];
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean[] getProbes() {
		return probes;
	}
}
