package com.jessebeau.commons.function;

import java.io.InputStream;
import java.util.function.Consumer;

@FunctionalInterface
public interface Parser<T extends InputStream, R> {
	R parse(T in) throws ParseException;

	default Parser<T, R> withLogger(Consumer<String> logger) {
		return in -> {
			try {
				return this.parse(in);
			} catch (ParseException e) {
				logger.accept(e.toString());
				throw e;
			}
		};
	}

	default Parser<T, R> orElse(R fallback) {
		return in -> {
			try {
				return this.parse(in);
			} catch (ParseException e) {
				return fallback;
			}
		};
	}

	class ParseException extends Exception {
		public ParseException(String message) {
			super(message);
		}

		public ParseException(Exception e) {
			super(e);
		}

		public ParseException(String message, Exception e) {
			super(message, e);
		}
	}
}



