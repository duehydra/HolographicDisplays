package com.gmail.filoghost.holograms.placeholders;

import org.bukkit.Bukkit;

public class MaxPlayers extends Placeholder {
	
	public MaxPlayers() {
		super("{max_players}", "{m}", 50); // 50 long ticks = 10 seconds (doesn't need to be updated often).

	}
	
	@Override
	public void update() {
		currentReplacement = Integer.toString(Bukkit.getMaxPlayers());
	}
	
}
