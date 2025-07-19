package com.jessebeau.commons.api.model;

import com.google.gson.JsonObject;

public class GsonLookup implements Lookup<String, Value> {
	private final JsonObject root;

	public GsonLookup(JsonObject root) {
		this.root = root;
	}

	@Override
	public Value get(String key) {
		var jsonElement = root.get(key);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			return Value.of(jsonElement.getAsString());
		} else {
			return Value.ofNull();
		}
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
