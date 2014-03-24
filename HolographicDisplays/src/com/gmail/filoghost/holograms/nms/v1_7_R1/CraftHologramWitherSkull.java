package com.gmail.filoghost.holograms.nms.v1_7_R1;

import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftWitherSkull;

public class CraftHologramWitherSkull extends CraftWitherSkull {

	public CraftHologramWitherSkull(CraftServer server, EntityHologramWitherSkull entity) {
		super(server, entity);
	}

	@Override
	public void remove() {
	    // Nope. Cannot be removed by bukkit plugins.
	}

}
