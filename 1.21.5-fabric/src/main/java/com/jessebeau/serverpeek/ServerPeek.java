package com.jessebeau.serverpeek;

import com.jessebeau.commons.PeekPlatform;
import com.jessebeau.serverpeek.command.Commands;
import com.jessebeau.serverpeek.platform.FabricServerDataProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class ServerPeek implements ModInitializer {
	private static PeekPlatform platform;

	public static PeekPlatform platform() {
		return platform;
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(ServerPeek::register);
		PeekPlatform.load(platform -> {
			ServerPeek.platform = platform;
			ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				FabricServerDataProvider.setServerInstance(server);
				platform.start();
			});
		});
	}

	private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
		dispatcher.register(Commands.build());
	}
}