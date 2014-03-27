package com.gmail.filoghost.holograms.utils;

import com.gmail.filoghost.holograms.nms.GenericEntityHologramHorse;

public class EntityAndNamePair {

	private GenericEntityHologramHorse horse;
	private String originalName;
	
	public EntityAndNamePair(GenericEntityHologramHorse horse, String originalName) {
		this.horse = horse;
		this.originalName = originalName;
	}

	public GenericEntityHologramHorse getHorse() {
		return horse;
	}

	public String getOriginalName() {
		return originalName;
	}
	
}
