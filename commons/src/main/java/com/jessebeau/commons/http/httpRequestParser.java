package com.jessebeau.commons.http;

import com.jessebeau.commons.api.Request;
import com.jessebeau.commons.api.Request.RequestBuilder;
import com.jessebeau.commons.function.Parser;

import java.io.*;

public final class httpRequestParser implements Parser<InputStream, Request> {
	@Override
	public Request parse(InputStream in) throws ParseException {
		try {
			var reader = new BufferedReader(new InputStreamReader(in));
			var builder = Request.builder();
			parseRequestLine(builder, reader.readLine());

			for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ) {
				parseHeader(builder, line);
			}

			var contentLengthValue = builder.getHeaderValue("Content-Length");
			if (contentLengthValue != null) {
				var contentLength = Integer.parseInt(contentLengthValue);
				readBody(builder, contentLength, reader);
			}

			return builder.create();
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	private static void readBody(RequestBuilder builder, int bodyLength, BufferedReader reader) throws IOException {
		char[] buff = new char[bodyLength];
		var bytesRead = reader.read(buff);
		builder.body(new String(buff, 0, bytesRead));
	}

	private static void parseHeader(RequestBuilder builder, String line) throws ParseException {
		var parts = line.split(": ", 2);
		if (parts.length != 2)
			throw new ParseException(String.format("Header line has invalid length (expected 2, got %d)", parts.length));
		builder.header(
				parts[0],
				parts[1]
		);
	}

	private static void parseRequestLine(RequestBuilder builder, String line) throws ParseException {
		var parts = line.split(" ");
		if (parts.length != 3)
			throw new ParseException(String.format("Request line has invalid length (expected 3, got %d)", parts.length));
		builder.method(parts[0])
				.path(parts[1])
			  	.version(parts[2]);
	}
}
