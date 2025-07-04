package com.jessebeau.serverpeek.platform;

import com.jessebeau.commons.platform.core.GameDataSource;
import net.minecraft.server.MinecraftServer;

public class FabricServerSource implements GameDataSource {
	private final MinecraftServer server;

	public FabricServerSource(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public int getPlayerCount() {
		return server.getCurrentPlayerCount();
	}
}
