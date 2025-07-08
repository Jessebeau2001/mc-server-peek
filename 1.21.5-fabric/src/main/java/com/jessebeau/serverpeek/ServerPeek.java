package com.jessebeau.serverpeek;

import com.jessebeau.commons.PeekPlatform;
import com.jessebeau.serverpeek.command.ServerPeekCommand;
import com.jessebeau.serverpeek.platform.FabricServerDataProvider;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class ServerPeek implements ModInitializer {
	@Override
	public void onInitialize() {
		PeekPlatform.load(PeekPlatform::start);
		CommandRegistrationCallback.EVENT.register(ServerPeek::registerCommands);
		ServerLifecycleEvents.SERVER_STARTED.register(FabricServerDataProvider::setServerInstance);
		ServerLifecycleEvents.SERVER_STOPPING.register(FabricServerDataProvider::clearServerInstance);
	}

	private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
		dispatcher.register(ServerPeekCommand.newArgument());
	}
}