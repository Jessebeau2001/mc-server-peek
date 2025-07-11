package com.jessebeau.serverpeek.command;

import com.jessebeau.commons.PeekPlatform;
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

public class ServerPeekCommand {
	private static final String CMD_ROOT = "server-peek";
	private static final String CMD_PORT = "port";
	private static final String CMD_DIALECT = "dialect";
	private static final String CMD_SET = "set";

	private static boolean isOperator(ServerCommandSource source) {
		// 2: Command blocks and ops
		// 4: only ops, e.g. players and host console (no command blocks)
		return source.hasPermissionLevel(4);
	}

	public static LiteralArgumentBuilder<ServerCommandSource> newArgument() {
		return literal(CMD_ROOT)
				.requires(ServerPeekCommand::isOperator)
				.then(PortCommand.newArgument())
				.then(DialectCommand.newArgument());
	}

	private static class PortCommand {
		public static LiteralArgumentBuilder<ServerCommandSource> newArgument() {
			return literal(CMD_PORT)
					.executes(PortCommand::get)
					.then(PortCommand.newSetArgument());
		}

		private static LiteralArgumentBuilder<ServerCommandSource> newSetArgument() {
			return literal(CMD_SET)
					.then(argument("port", IntegerArgumentType.integer(1, 65535))
							.executes(PortCommand::set));
		}

		private static int get(CommandContext<ServerCommandSource> context) {
			var port = PeekPlatform.getInstance().getPort();
			context.getSource().sendFeedback(() -> Text.literal("Current configured port is " + port), false);
			return 1;
		}

		private static int set(CommandContext<ServerCommandSource> context) {
			var port = IntegerArgumentType.getInteger(context, "port");
			PeekPlatform.getInstance().setPort(port);
			context.getSource().sendFeedback(() -> Text.literal("Set port to " + port), true);
			return 1;
		}
	}

	private static class DialectCommand {
		private static final SuggestionProvider<ServerCommandSource> DIALECT_SUGGESTIONS = ((context, builder) -> {
			Dialect.getNames().forEach(builder::suggest);
			return builder.buildFuture();
		});

		public static LiteralArgumentBuilder<ServerCommandSource> newArgument() {
			return literal(CMD_DIALECT)
					.executes(DialectCommand::get)
					.then(newSetArgument());
		}

		private static LiteralArgumentBuilder<ServerCommandSource> newSetArgument() {
			return literal(CMD_SET)
					.then(argument("dialect", StringArgumentType.string())
							.suggests(DIALECT_SUGGESTIONS)
							.executes(DialectCommand::set));
		}

		private static int get(CommandContext<ServerCommandSource> context) {
			var dialect = PeekPlatform.getInstance().getConfig().dialect();
			context.getSource().sendFeedback(() -> Text.literal(String.format("Current configured dialect is '%s'", dialect)), false);
			return 1;
		}

		private static int set(CommandContext<ServerCommandSource> context) {
			var dialectString = StringArgumentType.getString(context, "dialect");
			var dialect = Dialect.fromString(dialectString);
			if (dialect.isPresent()) {
				PeekPlatform.getInstance().getConfig().dialect(dialect.get());
				context.getSource().sendFeedback(() -> Text.literal(String.format("Dialect set to '%s'", dialect.get())), false);
				return 1;
			} else {
				context.getSource().sendError(Text.literal(String.format("Invalid dialect '%s'", dialectString)));
				return 0;
			}
		}
	}
}
