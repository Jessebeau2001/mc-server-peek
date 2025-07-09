package com.jessebeau.commons.api;

import com.jessebeau.commons.platform.core.PlatformHelper;

public class MinecraftRequestHandler implements RequestHandler {
	private final PlatformHelper platform;

	public MinecraftRequestHandler(PlatformHelper platform) {
		this.platform = platform;
	}

	@Override
	public void handle(Request request, Response response) {
		var provider = platform.getDataProvider();
		if (provider.isPresent()) {
			response.setStatus(200, "OK");
			response.getBody().put("message", "ok");
		} else {
			response.setStatus(503, "Service Unavailable");
		}
	}
}
