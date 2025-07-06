package com.jessebeau.commons.http;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Dialect {
	RAW("raw"),
	JSON("json");

	Dialect(String name) {
		this.name = name;
	}

	private final String name;

	@Override
	public String toString() {
		return this.name;
	}

	private static final Map<String, Dialect> VALUES = Arrays.stream(values())
			.collect(Collectors.toMap(d -> d.name, Function.identity()));

	public static Optional<Dialect> fromString(String name) {
		return Optional.ofNullable(VALUES.get(name));
	}
}
