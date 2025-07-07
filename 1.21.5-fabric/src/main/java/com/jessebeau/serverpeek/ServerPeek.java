package com.jessebeau.serverpeek;

import com.jessebeau.commons.PeekPlatform;
import com.jessebeau.serverpeek.command.Commands;
import com.jessebeau.serverpeek.platform.FabricServerDataProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.*;

public class ServerPeek implements ModInitializer {
	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(ServerPeek::register);
		PeekPlatform.load(() -> ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			FabricServerDataProvider.setServerInstance(server);
			PeekPlatform.start();
		}));
	}

	private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
		dispatcher.register(Commands.build());
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.of(server);
	}
}