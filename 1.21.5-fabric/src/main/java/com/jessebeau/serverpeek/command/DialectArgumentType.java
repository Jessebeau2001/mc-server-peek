package com.jessebeau.serverpeek.command;

import com.jessebeau.commons.http.Dialect;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.text.Text;

import java.util.Collection;

/*
	To register the argument type use the following,
	note this counts as 'client' code so it will not work for this mod

	ArgumentTypeRegistry.registerArgumentType(
			Identifier.of("server-peek", "dialect"),
			DialectArgumentType.class, ConstantArgumentSerializer.of(DialectArgumentType::dialect));
 */
public class DialectArgumentType implements ArgumentType<Dialect> {
	public static final DynamicCommandExceptionType INVALID_DIALECT = new DynamicCommandExceptionType(o -> Text.literal(String.format("Invalid dialect '%s'", o)));

	public static DialectArgumentType dialect() {
		return new DialectArgumentType();
	}

	public static <S> Dialect getDialect(CommandContext<S> context, String name) {
		return context.getArgument(name, Dialect.class);
	}

	@Override
	public Dialect parse(StringReader reader) throws CommandSyntaxException {
		var start = reader.getCursor();
		var dialectString = reader.readString();
		return Dialect.fromString(dialectString).orElseThrow(() -> {
			reader.setCursor(start);
			return INVALID_DIALECT.createWithContext(reader, "");
		});
	}

	@Override
	public Collection<String> getExamples() {
		return Dialect.getNames();
	}
}
