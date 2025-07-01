package com.jessebeau.commons.http;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import static com.jessebeau.commons.http.HttpHeader.CONTENT_LENGTH;
import static com.jessebeau.commons.http.HttpHeader.CONTENT_TYPE;

public class HttpResponseWriter extends FilterWriter {
	private static final String HTTP_VER = "HTTP/1.1";
	private static final String CRLF = "\r\n";

	private boolean headersWritten = false;

	public HttpResponseWriter(Writer out) {
		super(out);
	}

	public void writeStatus(int code, String message) throws IOException {
		out.write(HTTP_VER + " " + code + " " + message + CRLF);
	}

	private void writeHeader(String name, String value) throws IOException {
		out.write(name + ": " + value + CRLF);
	}

	private void writeHeader(HttpHeader content, String value) throws IOException {
		writeHeader(content.toString(), value);
	}

	public void writeContentType(ContentType type) throws IOException {
		writeHeader(CONTENT_TYPE, type.toString());
	}

	public void writeLength(int length) throws IOException {
		writeHeader(CONTENT_LENGTH, String.valueOf(length));
	}

	public void endHeaders() throws IOException {
		out.write(CRLF);
		headersWritten = true;
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		if (!headersWritten) {
			endHeaders();
		}
		super.write(str, off, len);
	}
}
