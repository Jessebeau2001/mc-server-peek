package com.jessebeau.commons.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Since;
import com.jessebeau.commons.http.Dialect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;

public class ModConfig {
	private static final Gson gson = new GsonBuilder()
			.registerTypeAdapter(Dialect.class, new DialectAdapter())
			.setPrettyPrinting()
			.create();
	private static ModConfig instance = null;

	private final File source;
	private Data data;
	private boolean autoSave = false;

	ModConfig(File source) {
		if (source == null)
			throw new IllegalArgumentException("File source cannot be null");

		this.source = source;
	}

	static void set(ModConfig config) {
		instance = config;
	}

	@Contract(pure = true)
	public static @NotNull Optional<ModConfig> get() {
		return Optional.ofNullable(instance);
	}

	public int port() {
		return data.port;
	}

	public void port(int port) {
		data.port = port;
		tryAutoSave();
	}

	public Dialect dialect() {
		return data.dialect;
	}

	public void dialect(Dialect dialect) {
		if (dialect == null)
			throw new RuntimeException("Dialect cannot be null");
		data.dialect = dialect;
		tryAutoSave();
	}

	public void load() throws IOException {
		try (var reader = new BufferedReader(new FileReader(this.source))) {
			this.data = gson.fromJson(reader, Data.class);
		}
	}

	public void save() throws IOException {
		try (var writer = new FileWriter(this.source)) {
			gson.toJson(this.data, writer);
		}
	}

	private void tryAutoSave() {
		if (autoSave) try {
			this.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setAutoSave(boolean enabled) {
		this.autoSave = enabled;
	}

	public static ModConfig defaultConfig(File file) {
		var config = new ModConfig(file);
		config.data = Data.defaultData();
		return config;
	}

	private static class Data {
		@Since(1.0)
		int port;
		Dialect dialect;

		// TODO: Move this to generated default config resource
		static Data defaultData() {
			var data = new Data();
			data.port = 8081;
			data.dialect = Dialect.RAW;
			return data;
		}
	}
}
