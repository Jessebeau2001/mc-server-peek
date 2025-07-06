package com.jessebeau.serverpeek.platform;

import com.jessebeau.commons.platform.core.GameDataSource;
import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.serverpeek.ServerPeek;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class FabricPlatformHelper implements PlatformHelper {
	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public GameDataSource getDataAdapter() {
		return new FabricServerSource(ServerPeek.getServer().orElseThrow());
	}

	@Override
	public File getConfigDir() {
		return FabricLoader.getInstance().getConfigDir().toFile();
	}
}
