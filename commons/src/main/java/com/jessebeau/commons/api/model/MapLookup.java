package com.jessebeau.commons.api.model;

import java.util.Map;

// Only opens get method for underlying map
public class MapLookup<K> implements Lookup<K, Value> {
	private final Map<K, String> map;

	public MapLookup(Map<K, String> map) {
		this.map = map;
	}

	public Value get(K key) {
		return new Value(map.get(key));
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		map.forEach((key, value) -> sb.append(key.toString())
				.append(": ")
				.append(value)
				.append("\n"));
		return sb.toString();
	}
}
