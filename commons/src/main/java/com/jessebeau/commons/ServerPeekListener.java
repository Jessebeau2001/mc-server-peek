package com.jessebeau.commons;

import com.jessebeau.commons.platform.ServiceFactory;
import com.jessebeau.commons.http.HttpResponseWriter;
import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.commons.platform.core.ServerDataProvider;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static com.jessebeau.commons.http.ContentType.TEXT_PLAIN;

public class ServerPeekListener {
	private static final int defaultPort = 8081;
	private static final String HOST = "localhost";

	private final int port;
	private final PlatformHelper platform;

	private volatile boolean enabled;
	private Thread listenerThread;
	private ServerSocket serverSocket;

	public ServerPeekListener(int port, PlatformHelper platform) {
		if (port <= 0)
			throw new IllegalArgumentException("Port cannot be a negative number");
		if (platform == null)
			throw new IllegalArgumentException("Game data adapter cannot be null");

		this.port = port;
		this.platform = platform;
	}

	public int getPort() {
		return this.port;
	}

	public void start() throws IOException {
		if (enabled) return;

		serverSocket = new ServerSocket(port, 0, InetAddress.getByName(HOST));
		enabled = true;
		listenerThread = new Thread(this::listen);
		listenerThread.setName("Peek Listener");
		listenerThread.start();
	}

	public void stop() throws IOException {
		enabled = false;
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		if (listenerThread != null) {
			try {
				listenerThread.join(); // Wait for graceful finish
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void listen() {
		while (enabled) try (var clientSocket = serverSocket.accept()) {
			handleRequest(clientSocket);
		} catch (SocketException e) {
			if (!enabled) break;
		} catch (IOException e) {
			throw new RuntimeException(e); // TODO: This can be better
		}
	}

	private void handleRequest(Socket client) throws IOException {
		System.out.println("Connected: " + client.getRemoteSocketAddress());

		var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		var out = new HttpResponseWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));

		String line;
		while ((line = in.readLine()) != null && !line.isEmpty()) {
			System.out.println(line);
		}

		var provider = platform.getDataProvider();
		if (provider.isPresent()) {
			var resp = String.valueOf(provider.get().getPlayerCount());
			out.writeStatus(200, "OK");
			out.writeContentType(TEXT_PLAIN);
			out.writeLength(resp.length());
			out.write(resp);
			out.flush();
		} else {
			final String resp = "Service Unavailable";
			out.writeStatus(503, resp);
			out.writeContentType(TEXT_PLAIN);
			out.writeLength(resp.length());
			out.write(resp);
			out.flush();
		}
	}

	public static ServerPeekListener newServerPeekListener(int port) {
		return new ServerPeekListener(port, ServiceFactory.newPlatformHelper());
	}

	public static ServerPeekListener defaultServerPeekListener() {
		return newServerPeekListener(defaultPort);
	}
}
