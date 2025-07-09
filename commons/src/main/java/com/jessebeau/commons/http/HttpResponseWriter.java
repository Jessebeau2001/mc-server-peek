package com.jessebeau.commons.http;

import com.jessebeau.commons.api.Response;
import com.jessebeau.commons.api.ResponseWriter;
import com.jessebeau.commons.data.Serializer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.jessebeau.commons.http.ContentType.JSON;
import static com.jessebeau.commons.http.HttpHeader.CONTENT_LENGTH;
import static com.jessebeau.commons.http.HttpHeader.CONTENT_TYPE;

public class HttpResponseWriter implements ResponseWriter, AutoCloseable {
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final String HTTP_VERSION = "HTTP/1.1";
	private static final String CRLF = "\r\n";

	private final BufferedWriter writer;
	private final Serializer<Response> serializer;

	public HttpResponseWriter(OutputStream out, Serializer<Response> serializer) {
		this.writer = new BufferedWriter(new OutputStreamWriter(out, CHARSET));
		this.serializer = serializer;
	}

	public void write(Response response) throws IOException {
		var payload = serializer.serialize(response);
		writeStatus(response.getStatusCode(), response.getStatusMessage());
		writeHeader(CONTENT_TYPE, JSON.mimeType());
		writeHeader(CONTENT_LENGTH, payload.length());
		endHeaders();
		writeBody(payload);
		writer.flush();
	}

	private void writeStatus(int code, String reason) throws IOException {
		writer.write(HTTP_VERSION + " " + code + " " + reason + CRLF);
	}

	private void writeHeader(String name, String value) throws IOException {
		writer.write(name + ": " + value + CRLF);
	}

	private void writeHeader(HttpHeader type, String value) throws IOException {
		writeHeader(type.toString(), value);
	}

	private void writeHeader(HttpHeader type, int value) throws IOException {
		writeHeader(type, String.valueOf(value));
	}

	private void endHeaders() throws IOException {
		writer.write(CRLF);
	}

	private void writeBody(String body) throws IOException {
		writer.write(body);
	}

	@Override
	public void close() throws Exception {
		writer.close();
	}
}
