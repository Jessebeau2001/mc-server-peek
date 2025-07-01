package com.jessebeau.commons;

import com.jessebeau.commons.core.GameDataSource;

import java.util.ArrayList;
import java.util.List;

public class GameDataStub implements GameDataSource {
	private final List<String> players = new ArrayList<>();

	private GameDataStub() {
		players.add("Jessebeau2001");
		players.add("JesseCam2001");
	}

	// Eager singleton
	private static final GameDataStub instance = new GameDataStub();

	public static GameDataStub getInstance() {
		return instance;
	}

	@Override
	public int getPlayerCount() {
		return players.size();
	}
}
