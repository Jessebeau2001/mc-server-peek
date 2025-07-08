package com.jessebeau.commons;

import com.jessebeau.commons.conf.ConfigLoader;
import com.jessebeau.commons.conf.ModConfig;
import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.commons.platform.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class PeekPlatform {
	private static final String MOD_ID = "server-peek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final PlatformHelper PLATFORM = ServiceFactory.newPlatformHelper();

	private static PeekPlatform instance;

	private final ModConfig config;
	private ServerPeekListener listener;

	private PeekPlatform(ModConfig config) {
		this.config = config;
	}

	public static synchronized void load(Consumer<PeekPlatform> onLoad) {
		if (isLoaded()) throw new IllegalStateException("Cannot load platform multiple times");
		LOGGER.info("Loading Peek Module for {}...", PLATFORM.getPlatformName());
		var config = ConfigLoader.tryLoad().orElseThrow();
		instance = new PeekPlatform(config);

		if (onLoad != null) {
			onLoad.accept(instance);
		}
	}

	public static boolean isLoaded() {
		return instance != null;
	}

	private static void assertLoaded() {
		if (!isLoaded()) throw new IllegalStateException("Platform not loaded");
	}

	public static PeekPlatform getInstance() {
		assertLoaded();
		return instance;
	}

	public void start() {
		if (listener == null) try {
			listener = ServerPeekListener.newServerPeekListener(config.port());
			listener.start();
			LOGGER.info("Started Server Peek on port {}.", + listener.getPort());
		} catch (IOException e) {
			LOGGER.error("An error occurred while starting Server Peek:\n{}", e.getMessage());
		}
	}

	public void stop() {
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

	public int getPort() {
		return this.config.port();
	}

	public void setPort(int port) {
		this.config.port(port);
	}

	public ModConfig getConfig() {
		return this.config;
	}
}
