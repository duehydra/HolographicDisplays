package com.gmail.filoghost.holograms.nms.v1_6_R3;

import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftWitherSkull;

public class CraftHologramWitherSkull extends CraftWitherSkull {

	public CraftHologramWitherSkull(CraftServer server, EntityHologramWitherSkull entity) {
		super(server, entity);
	}

	@Override
	public void remove() {
	    // Nope. Cannot be removed by bukkit plugins.
	}

}
