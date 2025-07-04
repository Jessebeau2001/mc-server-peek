package com.jessebeau.serverpeek;

import com.jessebeau.commons.Common;
import com.jessebeau.commons.ServerPeekListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class ServerPeek implements ModInitializer {
	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerPeek.server = server;
			Common.start();
		});
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.of(server);
	}
}