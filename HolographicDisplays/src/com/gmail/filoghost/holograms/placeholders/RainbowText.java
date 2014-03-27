package com.gmail.filoghost.holograms.placeholders;

public class RainbowText extends Placeholder {

	private String[] rainbowColors = new String[] {"§c", "§6", "§e", "§a", "§b", "§d"};
	private int colorsAmount = rainbowColors.length;
	private int index = 0;
	
	public RainbowText() {
		super("&u", "&u", 1);
	}
	
	@Override
	public void update() {
		index++;
		if (index >= colorsAmount) {
			index = 0;
		}
		
		currentReplacement = rainbowColors[index];
	}
	
}
