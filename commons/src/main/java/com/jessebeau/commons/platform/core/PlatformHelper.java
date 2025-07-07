package com.jessebeau.commons.platform.core;

import java.io.File;
import java.util.Optional;

public interface PlatformHelper {
	String getPlatformName();
	File getConfigDir();

	Optional<ServerDataProvider> getDataProvider();
}
