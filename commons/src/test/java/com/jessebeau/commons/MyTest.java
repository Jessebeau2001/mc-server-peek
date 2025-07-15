package com.jessebeau.commons;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class MyTest {
	private static final int PORT = 8081;
	private static PeekListener server;

	@BeforeAll
	static void startServer() throws IOException {
		server = new PeekListener(PORT, new MockPlatform());
		server.start();
	}

	@AfterAll
	static void stopServer() throws IOException {
		server.start();
	}

	@Test
	void get() throws Exception {
		try (var client = HttpClient.newHttpClient()) {
			var request = HttpRequest.newBuilder()
					.uri(new URI("http://localhost:" + PORT))
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
