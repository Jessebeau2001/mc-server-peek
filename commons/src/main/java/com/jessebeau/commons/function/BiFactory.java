package com.jessebeau.commons.function;

@FunctionalInterface
public interface BiFactory<T, U, R> {
	R create(T param1, U param2);
}
