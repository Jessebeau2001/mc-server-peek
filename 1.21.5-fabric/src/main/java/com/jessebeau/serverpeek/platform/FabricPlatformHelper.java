package com.jessebeau.serverpeek.platform;

import com.jessebeau.commons.platform.core.GameDataSource;
import com.jessebeau.commons.platform.PlatformHelper;
import com.jessebeau.serverpeek.ServerPeek;

public class FabricPlatformHelper implements PlatformHelper {
	@Override
	public String getPlatformName() {
		return "FabricMc";
	}

	@Override
	public GameDataSource getDataAdapter() {
		return new FabricServerSource(ServerPeek.getServer().orElseThrow());
	}
}
