package com.jessebeau.serverpeek.platform;

import com.jessebeau.commons.platform.core.ServerDataProvider;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class FabricServerDataProvider implements ServerDataProvider {
	private static ServerDataProvider instance;

	public static void clearServerInstance(MinecraftServer ignored) {
		instance = null;
	}

	public static void setServerInstance(MinecraftServer server) {
		instance = new FabricServerDataProvider(server);
	}

	public static Optional<ServerDataProvider> get() {
		return Optional.ofNullable(instance);
	}

	private final MinecraftServer server;

	public FabricServerDataProvider(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public int getPlayerCount() {
		return server.getCurrentPlayerCount();
	}
}
