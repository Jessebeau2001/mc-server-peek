package com.jessebeau.commons.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResponseSerializer {
	private static final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.serializeNulls()
			.create();

	public static String toJson(Response response) {
		return gson.toJson(response.getBody());
	}
}
