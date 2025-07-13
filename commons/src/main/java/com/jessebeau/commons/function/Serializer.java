package com.jessebeau.commons.function;

@FunctionalInterface
public interface Serializer<T> {
	String serialize(T t);
}
