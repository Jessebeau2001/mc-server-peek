package com.jessebeau.serverpeek.platform;

import com.jessebeau.commons.platform.core.ServerDataProvider;
import com.jessebeau.commons.platform.core.PlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.Optional;

public class FabricPlatformHelper implements PlatformHelper {
	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public Optional<ServerDataProvider> getDataProvider() {
		return FabricServerDataProvider.get();
	}

	@Override
	public File getConfigDir() {
		return FabricLoader.getInstance().getConfigDir().toFile();
	}
}
