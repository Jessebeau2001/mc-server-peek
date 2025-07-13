package com.jessebeau.commons;

import com.jessebeau.commons.api.*;
import com.jessebeau.commons.function.*;
import com.jessebeau.commons.http.HttpResponseWriter;
import com.jessebeau.commons.http.httpRequestParser;
import com.jessebeau.commons.platform.ServiceFactory;
import com.jessebeau.commons.platform.core.PlatformHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedHashMap;
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

	private PeekListener(
			@Range(from = 0, to = 65535) int port,
			@NotNull Parser<InputStream, Request> requestParser,
			@NotNull Handler<Request, Response> requestHandler,
			@NotNull Serializer<Response> responseSerializer,
			@NotNull BiFactory<OutputStream, Serializer<Response>, ResponseWriter> responseWriterFactory
	) {
		this.port 					= Preconditions.requireExclusiveRange(port, 0, 65535);
		this.requestParser 			= Objects.requireNonNull(requestParser);
		this.requestHandler 		= Objects.requireNonNull(requestHandler);
		this.responseSerializer 	= Objects.requireNonNull(responseSerializer);
		this.responseWriterFactory 	= Objects.requireNonNull(responseWriterFactory);
	}

	public PeekListener(
			@Range(from = 0, to = 65535) int port,
			@NotNull PlatformHelper platform
	) {
		this(port, new httpRequestParser(), new MinecraftRequestHandler(Objects.requireNonNull(platform)), ResponseSerializer::toJson, HttpResponseWriter::new);
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
//			readSocket(clientSocket.getInputStream());

			var response = new Response();
			var request = requestParser.parse(clientSocket.getInputStream());
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

	private void bufferedReadSocket(InputStream in) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(in));
		var requestLine = reader.readLine();
		var headerLines = new LinkedHashMap<String, String>();
		for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ) {
			var split = line.split(": ", 2);
			if (split.length != 2) System.out.println("could not parse: " + line);
			headerLines.put(split[0], split[1]);
		}

		System.out.println("Request: " + requestLine);
		headerLines.forEach((k, v) -> System.out.println("Header: " + k + ": " + v));

		var contentLength = Integer.parseInt(headerLines.get("Content-Length"));
		char[] buff = new char[contentLength];
		var bytesRead = reader.read(buff);
		System.out.println("Bytes read: " + bytesRead);
		System.out.println("Body:");
		System.out.println();
	}

	public Request tryReadSocket(InputStream in) throws Parser.ParseException {
		try {
			return readSocket(in);
		} catch (IOException e) {
			throw new Parser.ParseException(e);
		}
	}

	public Request readSocket(InputStream in) throws IOException {
		var reader = new BufferedReader(new InputStreamReader(in));
		var builder = Request.builder()
				.request(reader.readLine());

		int contentLength = -1;
		for (String line; (line = reader.readLine()) != null && !line.isEmpty(); ) {
			var split = line.split(": ", 2);
			if (split.length != 2) System.out.println("could not parse: " + line);
			builder.header(split[0], split[1]);
			if ("Content-Length".equals(split[0])) {
				contentLength = Integer.parseInt(split[1]);
			}
		}

		if (contentLength != -1) {
			char[] buff = new char[contentLength];
			var bytesRead = reader.read(buff);
			builder.body(new String(buff, 0, bytesRead));
		}
		var request = builder.create();
		request.print(System.out::println);
		return request;
	}

	public static PeekListener newPeekListener(int port) {
		return new PeekListener(port, ServiceFactory.newPlatformHelper());
	}

	public static PeekListener newPeekListener() {
		return newPeekListener(DEFAULT_PORT);
	}
}
