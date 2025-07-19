package com.jessebeau.commons.api.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Value {
	public static final Value NULL_VALUE = new Value(null);
	private final String value;

	Value(String value) {
		this.value = value;
	}

	/**
	 * @return a value containing a null string
	 * @see #NULL_VALUE
	 */
	@NotNull
	@Unmodifiable
	static Value ofNull() {
		return NULL_VALUE;
	}

	static Value of(String value) {
		return new Value(value);
	}

	public String get() {
		return this.value;
	}

	public int asInt() throws NumberFormatException {
		return Integer.parseInt(value);
	}

	public float asFloat() throws NumberFormatException {
		return Float.parseFloat(value);
	}

	public double asDouble() throws NumberFormatException {
		return Double.parseDouble(value);
	}

	/**
	 * Note that the passed in values are all sourced from a different machine which could, or rather will at some point, have a different
	 * DateTimeLocale than this machine.
	 */
	public LocalDateTime asLocalDateTime(@NotNull DateTimeFormatter formatter) throws DateTimeParseException {
		return LocalDateTime.parse(value, formatter);
	}

	@Override
	public String toString() {
		// TODO: Is this right?
		return value != null ? value : "null";
	}
}
