package com.jessebeau.commons;

import com.google.gson.JsonParseException;
import com.jessebeau.commons.conf.ConfigLoader;
import com.jessebeau.commons.conf.ModConfig;
import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.commons.platform.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PeekPlatform {
	private static final String MOD_ID = "server-peek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final PlatformHelper PLATFORM = ServiceFactory.newPlatformHelper();

	private static ServerPeekListener listener;

	public static void load(Runnable onLoad) {
		LOGGER.info("Loading Peek Module for {}...", PLATFORM.getPlatformName());
		try {
			ConfigLoader.load();
		} catch (JsonParseException e) {
			abortLoad("Failed to load config, is the json file malformed? (try deleting the config as a simple fix)", e);
			return;
		} catch (IOException e) {
			abortLoad("An IO Exception occurred while loading: " + e);
			return;
		}

		onLoad.run();
	}

	private static void abortLoad() {
		LOGGER.error("Aborting load...");
	}

	private static void abortLoad(String message) {
		LOGGER.error(message);
		abortLoad();
	}

	private static void abortLoad(String message, Exception e) {
		LOGGER.error(e.toString());
		LOGGER.error(message);
		abortLoad();
	}

	public static void start() {
		var config = ModConfig.get().orElseThrow();
		if (listener == null) try {
			listener = ServerPeekListener.newServerPeekListener(config.port());
			listener.start();
			LOGGER.info("Started Peek Server on port {}.", + listener.getPort());
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
