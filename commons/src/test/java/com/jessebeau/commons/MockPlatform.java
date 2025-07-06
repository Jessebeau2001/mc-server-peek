package com.jessebeau.commons;

import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.commons.platform.core.GameDataSource;

import java.io.File;

public class MockPlatform implements PlatformHelper {
	@Override
	public String getPlatformName() {
		return "JUnit-5";
	}

	@Override
	public GameDataSource getDataAdapter() {
		return GameDataStub.getInstance();
	}

	@Override
	public File getConfigDir() {
		return new File("./config/");
	}
}
