package com.gmail.filoghost.holograms.placeholders;

import java.util.ArrayList;
import java.util.List;

import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.utils.EntityAndNamePair;

public abstract class Placeholder {
	
	private String longPlaceholder;
	private String shortPlaceholder;
	
	// 1 longer refresh tick = 4 normal ticks
	private int longerRefreshTicks;
	
	protected String currentReplacement = ""; // To avoid exceptions, just use a blank string.
	
	private List<EntityAndNamePair> horsesToRefresh;
	
	public Placeholder(String longPlaceholder, String shortPlacehorser, int refreshTicks) {
		horsesToRefresh = new ArrayList<EntityAndNamePair>();
		this.longPlaceholder = longPlaceholder;
		this.shortPlaceholder = shortPlacehorser;
		this.longerRefreshTicks = refreshTicks;
	}
	
	public void trackIfNecessary(HologramHorse horse) {
		String customName = horse.getEntityCustomName();
		if (customName == null || customName.length() == 0) {
			return;
		}
		
		if (customName.contains(longPlaceholder)) {
			horsesToRefresh.add(new EntityAndNamePair(horse, customName.replace(longPlaceholder, shortPlaceholder)));
		}
	}
	
	public String longToShort(String input) {
		return input.replace(longPlaceholder, shortPlaceholder);
	}
	
	// This calculates the new text to replace the placeholder.
	public String getReplacement() {
		return currentReplacement;
	}
	
	public int getRefreshTicks() {
		return longerRefreshTicks;
	}
	
	public String getLongPlaceholder() {
		return longPlaceholder;
	}

	public String getShortPlaceholder() {
		return shortPlaceholder;
	}

	public abstract void update();

}
