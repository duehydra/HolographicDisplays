package com.gmail.filoghost.holograms.placeholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

public class PlaceholdersList {

	private static final Placeholder RAINBOW_TEXT = new AnimatedPlaceholder("&u", 1, new String[] {"§c", "§6", "§e", "§a", "§b", "§d"});
	
	private static final Placeholder ONLINE_PLAYERS = new Placeholder("{online}", "{o}", 5) {
		
		@Override
		public void update() {
			currentReplacement = Integer.toString(Bukkit.getOnlinePlayers().length);
		}
		
	};
	
	private static final Placeholder MAX_PLAYERS = new Placeholder("{max_players}", "{m}", 50) {
		
		@Override
		public void update() {
			currentReplacement = Integer.toString(Bukkit.getMaxPlayers());
		}
		
	};
	
	private static List<Placeholder> defaultList = Arrays.asList(RAINBOW_TEXT, ONLINE_PLAYERS, MAX_PLAYERS);
	private static List<AnimatedPlaceholder> animatedList = new ArrayList<AnimatedPlaceholder>();

	public static List<Placeholder> getDefaults() {
		return defaultList;
	}
	
	public static List<AnimatedPlaceholder> getAnimated() {
		return animatedList;
	}
	
	public static void clearAnimated() {
		animatedList.clear();
	}
	
	public static void addAnimatedPlaceholder(AnimatedPlaceholder animated) {
		if (!animatedList.contains(animated)) {
			animatedList.add(animated);
		}
	}
}
