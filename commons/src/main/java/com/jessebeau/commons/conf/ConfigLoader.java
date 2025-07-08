package com.jessebeau.commons.conf;

import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.jessebeau.commons.PeekPlatform.LOGGER;
import static com.jessebeau.commons.PeekPlatform.PLATFORM;

public class ConfigLoader {
	private static final String DEFAULT_FILE_NAME = "server-peek.json";

	public static Optional<ModConfig> tryLoad() {
		ModConfig config = null;
		try {
			config = load();
		} catch (JsonParseException e) {
			LOGGER.error(e.toString());
			LOGGER.error("Failed to load config, is the json file malformed? (try deleting the config as a simple fix)");
		} catch (IOException e) {
			LOGGER.error(e.toString());
			LOGGER.error("An IO Exception occurred while loading");
		}

		return Optional.ofNullable(config);
	}

	public static ModConfig load() throws IOException {
		var dir = initConfigDir();
		return initConfigFile(dir);
	}

	private static File initConfigDir() {
		var platformConfigDir = PLATFORM.getConfigDir();
		if (platformConfigDir.mkdirs())
			LOGGER.info("Created config directory '{}'", platformConfigDir.getAbsolutePath());
		return platformConfigDir;
	}

	private static ModConfig initConfigFile(File dir) throws IOException {
		var file = new File(dir, DEFAULT_FILE_NAME);
		ModConfig config;
		if (file.createNewFile()) {
			LOGGER.info("Created new config file '{}'", file.getName());
			config = ModConfig.defaultConfig(file);
			config.save();
		} else {
			config = new ModConfig(file);
			config.load();
		}
		return config;
	}
}
