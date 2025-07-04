package com.jessebeau.commons.platform;

public class ServiceNotFoundException extends RuntimeException {
	public ServiceNotFoundException(String name) {
		super(String.format("Service '%s' not found", name));
	}
}
