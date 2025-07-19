package com.jessebeau.commons.http;

import com.google.gson.JsonParser;
import com.jessebeau.commons.api.Request;
import com.jessebeau.commons.api.Request.RequestBuilder;
import com.jessebeau.commons.api.model.GsonLookup;
import com.jessebeau.commons.api.model.Lookup;
import com.jessebeau.commons.api.model.Value;
import com.jessebeau.commons.function.Parser;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

import static com.jessebeau.commons.http.HttpHeader.*;

public final class HttpRequestParser implements Parser<InputStream, Request> {
	@Override
	public Request parse(InputStream in) throws ParseException {
		try {
			var reader = new BufferedReader(new InputStreamReader(in));
			var builder = Request.builder();
			parseRequestLine(builder, reader.readLine());
			var headers = parseHeaders(reader);
			builder.headers(headers);

			// TODO: Many improvements possible here, first and foremost JsonParser being able to read the stream itself
			var contentLengthValue = headers.get(CONTENT_LENGTH.name()); // Use builders value for if headers ever become non-map polymorphic type
			if (contentLengthValue != null) {
				var contentLength = Integer.parseInt(contentLengthValue);
				var rawBody = readBody(reader, contentLength);
				var body = parseBody(headers, rawBody);
				builder.body(body);
			}

			return builder.create();
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	private static String readBody(BufferedReader reader, int length) throws IOException {
		char[] buff = new char[length];
		var bytesRead = reader.read(buff);
		return new String(buff, 0, bytesRead);
	}

	private static Lookup<String, Value> parseBody(Map<String, String> headers, String body) {
		var mimeType = headers.get(CONTENT_TYPE.name());
		var contentType = ContentType.ofMimeType(mimeType);
		// Replace with switch when more support is added
		if (contentType == ContentType.JSON) {
			var jsonObject = JsonParser.parseString(body).getAsJsonObject();
			return new GsonLookup(jsonObject);
		}
		return null;
	}

	private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException, ParseException {
		var headers = new TreeMap<String, String>();
		for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ) {
			parseHeader(headers, line);
		}
		return headers;
	}

	private static void parseHeader(Map<String, String> map, String line) throws ParseException {
		var parts = line.split(": ", 2);
		if (parts.length != 2)
			throw new ParseException(String.format("Header line has invalid length (expected 2, got %d)", parts.length));
		map.put(
				parts[0], // Header key
				parts[1]  // Header value
		);
	}

	private static void parseRequestLine(RequestBuilder builder, String line) throws ParseException {
		var parts = line.split(" ");
		if (parts.length != 3)
			throw new ParseException(String.format("Request line has invalid length (expected 3, got %d)", parts.length));
		builder.method(parseMethod(parts[0]))
				.path(parts[1])
			  	.version(parts[2]);
	}

	private static Method parseMethod(String value) throws ParseException {
		try {
			return Method.valueOf(value);
		} catch (IllegalArgumentException e) {
			throw new ParseException(value + " is not a valid HTTP method", e);
		}
	}
}
