package com.jessebeau.commons.http;

public enum ContentType {
	TEXT_PLAIN("text/plain"),
	JSON("application/json"),
	HTML("text/html"),
	OCTET("application/octet-stream");

	private final String mimeType;

	ContentType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String mimeType() {
		return this.mimeType;
	}

	@Override
	public String toString() {
		return mimeType;
	}
}
