package com.gmail.filoghost.holograms.utils;

import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;

public class EntityAndNamePair {

	private HologramHorse horse;
	private String originalName;
	
	public EntityAndNamePair(HologramHorse horse, String originalName) {
		this.horse = horse;
		this.originalName = originalName;
	}

	public HologramHorse getHorse() {
		return horse;
	}

	public String getSavedName() {
		return originalName;
	}
	
}
