package com.jessebeau.commons.api;

import com.jessebeau.commons.http.Method;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Request {
	// Http request line
	private Method method;
	private String path;
	private String version;
	// Headers
	private final HeaderMap headers;
	// Body
	private final String body; // Make some abstract version of map. E.g. need to be able to look up values in the body from outside request class

	private Request(Method method, String path, String version, HeaderMap headers, String body) {
		this.method = method;
		this.path = path;
		this.version = version;
		this.headers = headers;
		this.body = body;
	}

	public static RequestBuilder builder() {
		return new RequestBuilder();
	}

	public Method method() {
		return this.method;
	}

	public String path() {
		return this.path;
	}

	public String version() {
		return this.version;
	}

	public HeaderMap headers() {
		return this.headers;
	}

	public void print(@NotNull Consumer<String> printer) {
		printer.accept(method + " " + path + " " + version);
		headers.forEach((k, v) -> printer.accept(k + ": " + v));
		printer.accept("");
		printer.accept(body);
	}

	public static class HeaderMap {
		private final Map<String, String> map;

		private HeaderMap() {
			this.map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		}

		public HeaderValue get(String key) {
			return new HeaderValue(map.get(key));
		}

		private void set(String key, String value) {
			map.put(key, value);
		}

		public void forEach(BiConsumer<String, String> action) {
			map.forEach(action);
		}

		public static class HeaderValue {
			private final String value;

			private HeaderValue(String value) {
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
		// Http request line
		private Method method;
		private String path;
		private String version;
		// Headers
		private final HeaderMap headers;
		// Body
		private String body;

		private RequestBuilder() {
			this.headers = new HeaderMap();
		}

		public RequestBuilder method(Method method) {
			this.method = method;
			return this;
		}

		public RequestBuilder path(String path) {
			this.path = path;
			return this;
		}

		public RequestBuilder version(String version) {
			this.version = version;
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
			return new Request(method, path, version, headers, body);
		}

		public String getHeaderValue(String key) {
			return headers.map.get(key);
		}
	}
}
