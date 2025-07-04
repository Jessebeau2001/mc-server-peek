package com.jessebeau.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Common {
	private static final String MOD_ID = "server-peek";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static ServerPeekListener listener;

	public static void start() {
		if (listener == null) try {
			listener = ServerPeekListener.defaultServerPeekListener();
			listener.start();
			LOGGER.info("Started Server Peek server on {}.", + ServerPeekListener.defaultPort);
		} catch (IOException e) {
			LOGGER.error("An error occurred while starting Server Peek:\n{}", e.getMessage());
		}
	}

	public static void stop() {
		if (listener != null) {
			try {
				listener.stop();
				listener = null;
				LOGGER.info("Stopped Server Peek server.");
			} catch (IOException e) {
				LOGGER.error("An error occurred while stopping Server Peek:\n{}", e.getMessage());
			}
		}
	}
}
