package com.gmail.filoghost.holograms.placeholders;

import org.bukkit.Bukkit;

public class OnlinePlayers extends Placeholder {
	
	public OnlinePlayers() {
		super("{online}", "{o}", 5); // 5 long ticks = 1 second.

	}
	
	@Override
	public void update() {
		currentReplacement = Integer.toString(Bukkit.getOnlinePlayers().length);
	}
	
}
