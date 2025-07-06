package com.jessebeau.commons.platform.core;

import java.io.File;

public interface PlatformHelper {
	String getPlatformName();
	GameDataSource getDataAdapter();
	File getConfigDir();
}
