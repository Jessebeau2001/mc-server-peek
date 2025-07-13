package com.jessebeau.commons.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Response {
	private final HashMap<String, String> body;

	private int statusCode;
	private String statusMessage;

	public Response() {
		this.body = new LinkedHashMap<>();
	}

	public void setStatus(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void set(String key, String value) {
		body.put(key, value);
	}

	public HashMap<String, String> getBody() {
		return this.body;
	}
}
