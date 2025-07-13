package com.jessebeau.commons.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Request {
	private final String request;
	private final HeaderMap headers;
	private final String body; // Make some abstract version of map. E.g. need to be able to look up values in the body from outside request class

	private Request(String request, HeaderMap headers, String body) {
		this.request = request;
		this.headers = headers;
		this.body = body;
	}

	public static RequestBuilder builder() {
		return new RequestBuilder();
	}

	public @Nullable String getHeader(String key) {
		return headers.get(key).get();
	}

	public void print(@NotNull Consumer<String> printer) {
		printer.accept("Request: " + request);
		headers.forEach((k, v) -> printer.accept("Header: " + k + ": " + v));
		printer.accept("Body:");
		printer.accept(body);
	}

	public static class HeaderMap {
		private final Map<String, StringWrapper> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		public StringWrapper get(String key) {
			return map.get(key);
		}

		private void set(String key, String value) {
			map.put(key, new StringWrapper(value));
		}

		public void forEach(BiConsumer<String, StringWrapper> action) {
			map.forEach(action);
		}

		public static class StringWrapper {
			private final String value;

			private StringWrapper(String value) {
				this.value = value;
			}

			public String get() {
				return this.value;
			}

			public int asInt() throws NumberFormatException {
				return Integer.parseInt(value);
			}

			public float asFloat() throws NumberFormatException {
				return Float.parseFloat(value);
			}

			public double asDouble() throws NumberFormatException {
				return Double.parseDouble(value);
			}

			/**
			 * Note that the passed in values are all sourced from a different machine which could, or rather will at some point, have a different
			 * DateTimeLocale than this machine.
			 */
			public LocalDateTime asLocalDateTime(@NotNull DateTimeFormatter formatter) throws DateTimeParseException {
				return LocalDateTime.parse(value, formatter);
			}

			@Override
			public String toString() {
				return value;
			}
		}
	}

	public static class RequestBuilder {
		private final HeaderMap headers;
		private String method;
		private String request;
		private String body;

		private RequestBuilder() {
			headers = new HeaderMap();
		}

		public RequestBuilder method(String method) {
			this.method = method;
			return this;
		}

		public RequestBuilder request(String request) {
			this.request = request;
			return this;
		}

		public RequestBuilder header(String key, String value) {
			headers.set(key, value);
			return this;
		}

		public RequestBuilder body(String body) {
			this.body = body;
			return this;
		}

		public Request create() {
			return new Request(request, headers, body);
		}
	}
}
