package com.jessebeau.commons.http;

public enum HttpHeader {
	CONTENT_ENCODING("Content-Encoding"),
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length");

	private final String name;

	HttpHeader(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
