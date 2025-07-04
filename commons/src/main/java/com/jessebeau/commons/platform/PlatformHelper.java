package com.jessebeau.commons.platform;

import com.jessebeau.commons.platform.core.GameDataSource;

public interface PlatformHelper {
	String getPlatformName();
	GameDataSource getDataAdapter();
}
