package com.jessebeau.serverpeek.command;

import com.jessebeau.commons.PeekPlatform;
import com.jessebeau.commons.conf.ModConfig;
import com.jessebeau.commons.http.Dialect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
	private static final ModConfig CONFIG = ModConfig.get().orElseThrow();

	private static final String CMD_ROOT = "server-peek";
	private static final String CMD_PORT = "port";
	private static final String CMD_DIALECT = "dialect";
	private static final String CMD_SET = "set";

	private static final SuggestionProvider<ServerCommandSource> DIALECT_SUGGESTIONS = ((context, builder) -> {
		Dialect.getNames().forEach(builder::suggest);
		return builder.buildFuture();
	});

	private static boolean isOperator(ServerCommandSource source) {
		// 2: Command blocks and ops
		// 4: only ops, e.g. players and host console (no command blocks)
		return source.hasPermissionLevel(4);
	}

	public static LiteralArgumentBuilder<ServerCommandSource> buildPeekCommand() {
		return literal(CMD_ROOT)
				.requires(Commands::isOperator)
				.then(buildPortCommand())
				.then(buildDialectCommand());
	}

	private static LiteralArgumentBuilder<ServerCommandSource> buildPortCommand() {
		return literal(CMD_PORT)
				.executes(Commands::getPortExecutor)
				.then(buildSetPortCommand());
	}

	private static LiteralArgumentBuilder<ServerCommandSource> buildSetPortCommand() {
		return literal(CMD_SET)
				.then(argument("port", IntegerArgumentType.integer(1, 65535))
						.executes(Commands::setPortExecutor));
	}

	private static LiteralArgumentBuilder<ServerCommandSource> buildDialectCommand() {
		return literal(CMD_DIALECT)
				.executes(Commands::getDialectExecutor)
				.then(buildSetDialectCommand());
	}

	private static LiteralArgumentBuilder<ServerCommandSource> buildSetDialectCommand() {
		return literal(CMD_SET)
				.then(argument("dialect", StringArgumentType.string())
						.suggests(DIALECT_SUGGESTIONS)
						.executes(Commands::setDialectExecutor));
	}

	private static int getPortExecutor(CommandContext<ServerCommandSource> context) {
		var port = PeekPlatform.getPort();
		context.getSource().sendFeedback(() -> Text.literal("Current configured port is " + port), false);
		return 1;
	}

	private static int setPortExecutor(CommandContext<ServerCommandSource> context) {
		var port = IntegerArgumentType.getInteger(context, "port");
		PeekPlatform.setPort(port);
		context.getSource().sendFeedback(() -> Text.literal("Set port to " + port), true);
		return 1;
	}

	private static int getDialectExecutor(CommandContext<ServerCommandSource> context) {
		var dialect = CONFIG.dialect();
		context.getSource().sendFeedback(() -> Text.literal(String.format("Current configured dialect is '%s'", dialect)), false);
		return 1;
	}

	private static int setDialectExecutor(CommandContext<ServerCommandSource> context) {
		var dialectString = StringArgumentType.getString(context, "dialect");
		var dialect = Dialect.fromString(dialectString);
		if (dialect.isPresent()) {
			context.getSource().sendFeedback(() -> Text.literal(String.format("Dialect set to '%s'", dialect.get())), false);
			return 1;
		} else {
			context.getSource().sendError(Text.literal(String.format("Invalid dialect '%s'", dialectString)));
			return 0;
		}
	}
}
