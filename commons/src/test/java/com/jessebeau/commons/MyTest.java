package com.jessebeau.commons;

import com.jessebeau.commons.api.*;
import com.jessebeau.commons.function.Handler;
import com.jessebeau.commons.http.HttpResponseWriter;
import com.jessebeau.commons.http.Method;
import com.jessebeau.commons.http.httpRequestParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.jessebeau.commons.http.Method.GET;
import static org.junit.jupiter.api.Assertions.*;

public class MyTest {
	private static final int PORT = 8081;
	private static PeekListener server;

	@BeforeAll
	static void startServer() throws IOException {
		server = new PeekListener(
				PORT,
				new httpRequestParser(),
				newCustomHandler(),
//				new MinecraftRequestHandler(new MockPlatform()),
				ResponseSerializer::toJson,
				HttpResponseWriter::new
		);
		server.start();
	}

	private static Handler<Request, Response> newCustomHandler() {
		var router = new RequestRouter();
		router.register(GET, "/", (req, res) -> {
			res.setStatus(200, "OK");
			res.getBody().put("path", "/");
		});
		router.register(GET, "/test", (req, res) -> {
			res.setStatus(200, "OK");
			res.getBody().put("path", "/test <- it works!!");
		});
		return router;
	}

	@AfterAll
	static void stopServer() throws IOException {
		server.start();
	}

	@Test
	void get() throws Exception {
		try (var client = HttpClient.newHttpClient()) {
			var request = HttpRequest.newBuilder()
					.uri(new URI("http://localhost:" + PORT + "/test"))
					.GET()
					.build();

			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			printResponse(response);
		}
	}

	@Test
	void post() throws Exception {
		try (var client = HttpClient.newHttpClient()) {
			var request = HttpRequest.newBuilder()
					.uri(new URI("http://localhost:" + PORT))
					.POST(HttpRequest.BodyPublishers.ofString("your request body here"))
					.build();
			var response = client.send(request, HttpResponse.BodyHandlers.ofString());
			printResponse(response);
		}
	}

	private static void printResponse(HttpResponse<String> response) {
		System.out.println("=".repeat(20));
		response.headers().map().forEach((k, v) -> System.out.println(k + ": " + v.toString()));
		System.out.println(response.body());
	}
}
