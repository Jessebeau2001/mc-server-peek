package com.jessebeau.commons;

import com.jessebeau.commons.platform.core.PlatformHelper;
import com.jessebeau.commons.platform.core.ServerDataProvider;

import java.io.File;
import java.util.Optional;

public class MockPlatform implements PlatformHelper {
	@Override
	public String getPlatformName() {
		return "JUnit-5";
	}

	@Override
	public Optional<ServerDataProvider> getDataProvider() {
		return Optional.of(StubDataProvider.getInstance());
	}

	@Override
	public File getConfigDir() {
		return new File("./config/");
	}
}
