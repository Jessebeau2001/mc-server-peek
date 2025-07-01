package com.jessebeau.serverpeek;

import com.jessebeau.commons.ServerPeekListener;
import com.jessebeau.serverpeek.core.FabricServerSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerPeek implements ModInitializer {
	private ServerPeekListener listener;

	public static final String MOD_ID = "server-peek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			try {
				listener = new ServerPeekListener(8081, new FabricServerSource(server));
				listener.start();
				LOGGER.info("Started listener on 8081");
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		});
	}
}