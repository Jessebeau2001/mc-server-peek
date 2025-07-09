package com.jessebeau.commons.data;

public interface Serializer<T> {
	String serialize(T object);
}
