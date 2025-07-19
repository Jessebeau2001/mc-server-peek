package com.jessebeau.commons.api;

import com.jessebeau.commons.api.model.Lookup;
import com.jessebeau.commons.api.model.MapLookup;
import com.jessebeau.commons.api.model.Value;
import com.jessebeau.commons.http.Method;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Request {
	// Http request line
	private final Method method;
	private final String path;
	private final String version;
	// Headers
	private final Lookup<String, Value> headers;
	// Body
	private final Lookup<String, Value> body; // Make some abstract version of map. E.g. need to be able to look up values in the body from outside request class

	private Request(Method method, String path, String version, Lookup<String, Value> headers, Lookup<String, Value> body) {
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

	public Lookup<String, Value> headers() {
		return this.headers;
	}

	public Lookup<String, Value> body() {
		return this.headers;
	}

	public void print(@NotNull Consumer<String> printer) {
		printer.accept(method + " " + path + " " + version);
		printer.accept(headers.toString());
		if (body != null) {
			printer.accept("");
			printer.accept(body.toString());
		}
	}

	public static class RequestBuilder {
		// Http request line
		private Method method;
		private String path;
		private String version;
		// Parsed Content
		private @Nullable Lookup<String, Value> headers = null;
		private @Nullable Lookup<String, Value> body = null;

		private RequestBuilder() { }

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

		public RequestBuilder headers(Lookup<String, Value> headers) {
			this.headers = headers;
			return this;
		}

		public RequestBuilder body(Lookup<String, Value> body) {
			this.body = body;
			return this;
		}

		public RequestBuilder headers(Map<String, String> map) {
			return headers(new MapLookup<>(map));
		}

		public RequestBuilder body(Map<String, String> map) {
			return body(new MapLookup<>(map));
		}

		public Request create() {
			Objects.requireNonNull(headers, "Cannot construct request without headers");
			return new Request(
					method,
					path,
					version,
					headers,
					body
			);
		}
	}
}
