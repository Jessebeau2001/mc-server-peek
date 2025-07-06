package com.jessebeau.commons.platform;

import com.jessebeau.commons.platform.core.PlatformHelper;

import java.util.ServiceLoader;

public class ServiceFactory {
	private static <S> S loadService(Class<S> service) {
		var loader = ServiceLoader.load(service);
		return loader.findFirst()
				.orElseThrow(() -> new ServiceNotFoundException(service.getSimpleName()));
	}

	public static PlatformHelper newPlatformHelper() {
		return loadService(PlatformHelper.class);
	}
}
