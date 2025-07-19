package com.jessebeau.commons.api.model;

public interface Lookup<K, V> {
	V get(K key);
}
