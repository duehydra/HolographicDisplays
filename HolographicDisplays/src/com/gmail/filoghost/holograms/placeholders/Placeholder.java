package com.gmail.filoghost.holograms.placeholders;

import org.bukkit.Bukkit;

public enum Placeholder {

	RAINBOW_TEXT("&u", "&u", 1) {

		private String[] rainbowColors = new String[] {"§c", "§6", "§e", "§a", "§b", "§d"};
		private int index = 0;
		
		@Override
		public void update() {
			currentReplacement = rainbowColors[index];
			
			index++;
			if (index >= rainbowColors.length) {
				index = 0;
			}
		}
	},
	
	ONLINE_PLAYERS("{online}", "{o}", 5) {
		
		@Override
		public void update() {
			currentReplacement = Integer.toString(Bukkit.getOnlinePlayers().length);
		}
	},
	
	MAX_PLAYERS("{max_players}", "{m}", 50) {
		
		@Override
		public void update() {
			currentReplacement = Integer.toString(Bukkit.getMaxPlayers());
		}
	};
	
	private String longPlaceholder;
	private String shortPlaceholder;
	
	// 1 longer refresh tick = 4 normal ticks = 1/5 of second.
	private int longerRefreshTicks;
	
	// To avoid exceptions, just use a blank string. This will be used by the implementation.
	protected String currentReplacement = "";
	
	private Placeholder(String longPlaceholder, String shortPlaceholder, int longTicks) {
		this.longPlaceholder = longPlaceholder;
		this.shortPlaceholder = shortPlaceholder;
		this.longerRefreshTicks = longTicks;
	}
	
	public int getLongRefreshTicks() {
		return longerRefreshTicks;
	}
	
	public String getLongPlaceholder() {
		return longPlaceholder;
	}
	
	public String getShortPlaceholder() {
		return shortPlaceholder;
	}
	
	public abstract void update();

	public CharSequence getReplacement() {
		return currentReplacement;
	}

}
