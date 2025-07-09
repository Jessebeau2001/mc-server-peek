package com.jessebeau.commons.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jessebeau.commons.data.Serializer;

public class JsonResponseSerializer implements Serializer<Response> {
	private final Gson gson;

	public JsonResponseSerializer() {
		this.gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.create();
	}

	@Override
	public String serialize(Response response) {
		return gson.toJson(response.getBody());
	}
}
