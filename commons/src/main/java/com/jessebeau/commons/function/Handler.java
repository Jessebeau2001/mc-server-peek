package com.jessebeau.commons.function;

import java.util.Objects;

@FunctionalInterface
public interface Handler<T, E> {
	void handle(T in, E out);

	default Handler<T, E> andThen(Handler<T, E> after) {
		Objects.requireNonNull(after);
		return (t, u) -> {
			this.handle(t, u);
			after.handle(t, u);
		};
	}
}
