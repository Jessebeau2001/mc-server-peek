package com.jessebeau.commons.conf;

import java.io.File;
import java.io.IOException;

import static com.jessebeau.commons.PeekPlatform.LOGGER;
import static com.jessebeau.commons.PeekPlatform.PLATFORM;

public class ConfigLoader {
	private static final String defaultFileName = "server-peek.json";

	public static void load() throws IOException {
		var dir = initPlatformDir();
		var config = initConfigFile(dir);
		ModConfig.set(config);
	}

	private static File initPlatformDir() {
		var platformConfigDir = PLATFORM.getConfigDir();
		if (platformConfigDir.mkdirs())
			LOGGER.info("Created config directory '{}'", platformConfigDir.getAbsolutePath());
		return platformConfigDir;
	}

	private static ModConfig initConfigFile(File dir) throws IOException {
		var file = new File(dir, defaultFileName);
		ModConfig config;
		if (file.createNewFile()) {
			LOGGER.info("Created new config file '{}'", file.getName());
			config = ModConfig.defaultConfig(file);
			config.save();
		} else {
			config = new ModConfig(file);
			config.load();
		}
		config.setAutoSave(true);
		return config;
	}

}
