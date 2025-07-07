package com.jessebeau.commons;

import com.jessebeau.commons.platform.core.ServerDataProvider;

import java.util.ArrayList;
import java.util.List;

public class StubDataProvider implements ServerDataProvider {
	private final List<String> players = new ArrayList<>();

	private StubDataProvider() {
		players.add("Jessebeau2001");
		players.add("JesseCam2001");
	}

	// Eager singleton
	private static final StubDataProvider instance = new StubDataProvider();

	public static StubDataProvider getInstance() {
		return instance;
	}

	@Override
	public int getPlayerCount() {
		return players.size();
	}
}
