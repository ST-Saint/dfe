package edu.purdue.dfe.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataStorage {

	// FIXME concurrency?
	private final Map<Long, ProbeData> entries = new HashMap<Long, ProbeData>();

	private final Set<String> names = new HashSet<String>();

	public DataStorage() {

	}

	public ProbeData get(Long id, String name, int probeCount) {
		// FIXME avoid collision
		if (entries.containsKey(id)) {
			return entries.get(id);
		} else {
			ProbeData probeData = new ProbeData(id, name, probeCount);
			entries.put(id, probeData);
			return probeData;
		}
	}
}
