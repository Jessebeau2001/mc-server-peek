package com.jessebeau.commons;

import com.jessebeau.commons.api.*;
import com.jessebeau.commons.function.*;
import com.jessebeau.commons.http.HttpResponseWriter;
import com.jessebeau.commons.http.HttpRequestParser;
import com.jessebeau.commons.platform.ServiceFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Objects;

public class PeekListener {
	private static final int DEFAULT_PORT = 8081;
	private static final String HOSTNAME = "localhost";

	private final Parser<InputStream, Request> requestParser;
	private final Handler<Request, Response> requestHandler;
	private final Serializer<Response> responseSerializer;
	private final BiFactory<OutputStream, Serializer<Response>, ResponseWriter> responseWriterFactory;

	private int port;
	private volatile boolean enabled;
	private Thread listenerThread;
	private ServerSocket serverSocket;

	public PeekListener(
			@Range(from = 0, to = 65535) int port,
			@NotNull Parser<InputStream, Request> requestParser,
			@NotNull Handler<Request, Response> requestHandler,
			@NotNull Serializer<Response> responseSerializer,
			@NotNull BiFactory<OutputStream, Serializer<Response>, ResponseWriter> responseWriterFactory
	) {
		this.port = Preconditions.requireExclusiveRange(port, 0, 65535);
		this.requestParser = Objects.requireNonNull(requestParser);
		this.requestHandler = Objects.requireNonNull(requestHandler);
		this.responseSerializer = Objects.requireNonNull(responseSerializer);
		this.responseWriterFactory 	= Objects.requireNonNull(responseWriterFactory);
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		if (enabled) throw new IllegalStateException("Cannot change the port in an active state");
		this.port = port;
	}

	public void start() throws IOException {
		if (enabled) return;

		enabled = true;
		serverSocket = new ServerSocket(port, 0, InetAddress.getByName(HOSTNAME));
		listenerThread = new Thread(this::listen);
		listenerThread.setName("Peek Listener");
		listenerThread.start();
	}

	public void stop() throws IOException {
		enabled = false;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} finally {
			if (listenerThread != null) try {
				listenerThread.join(); // Wait for graceful finish
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			serverSocket = null;
			listenerThread = null;
		}
	}

	private void listen() {
		while (enabled) try (var clientSocket = serverSocket.accept()) {
			System.out.println("Connected: " + clientSocket.getRemoteSocketAddress());
			var request = requestParser.parse(clientSocket.getInputStream());
			request.print(System.out::println);
			var response = new Response(501, "Not Implemented");
			requestHandler.handle(request, response);
			responseWriterFactory.create(clientSocket.getOutputStream(), responseSerializer).write(response);
		} catch (SocketException e) {
			if (!enabled) break;
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
			throw new RuntimeException(e); // TODO: This can be better
		} catch (Parser.ParseException e) {
			System.err.println(e);
			throw new RuntimeException(e);
		}
	}

	public static PeekListener newMinecraftPeekListener() {
		var platform = ServiceFactory.newPlatformHelper();
		return new PeekListener(
				DEFAULT_PORT,
				new HttpRequestParser(),
				new MinecraftRequestHandler(Objects.requireNonNull(platform)),
				ResponseSerializer::toJson,
				HttpResponseWriter::new
		);
	}
}
