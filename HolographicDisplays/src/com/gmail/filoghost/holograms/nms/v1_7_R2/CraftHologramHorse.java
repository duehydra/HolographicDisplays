package com.gmail.filoghost.holograms.nms.v1_7_R2;

import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftHorse;

public class CraftHologramHorse extends CraftHorse {

	public CraftHologramHorse(CraftServer server, EntityHologramHorse entity) {
		super(server, entity);
	}
	
	@Override
	public void remove() {
	    // Nope. Cannot be removed by bukkit plugins.
	}
}
