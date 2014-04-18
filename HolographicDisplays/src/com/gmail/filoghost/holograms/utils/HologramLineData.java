package com.gmail.filoghost.holograms.utils;

import java.util.List;

import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.placeholders.Placeholder;

public class HologramLineData {

	private HologramHorse horse;
	private String originalName;
	private Placeholder[] containedPlaceholders;
	private String[] containedBungeeServers;
	
	public HologramLineData(HologramHorse horse, String originalName) {
		this.horse = horse;
		this.originalName = originalName;
	}
	
	public void setContainedPlaceholders(List<Placeholder> list) {
		containedPlaceholders = new Placeholder[list.size()];
		containedPlaceholders = list.toArray(containedPlaceholders);
	}
	
	public void setContainedBungeeServers(List<String> list) {
		containedBungeeServers = new String[list.size()];
		containedBungeeServers = list.toArray(containedBungeeServers);
	}

	public HologramHorse getHorse() {
		return horse;
	}

	public String getSavedName() {
		return originalName;
	}
	
	public boolean hasPlaceholders() {
		return containedPlaceholders != null;
	}
	
	public boolean hasBungeeServers() {
		return containedBungeeServers != null;
	}
	
	/**
	 * Can be null.
	 */
	public Placeholder[] getPlaceholders() {
		return containedPlaceholders;
	}
	
	/**
	 * Can be null.
	 */
	public String[] getBungeeServers() {
		return containedBungeeServers;
	}
}
