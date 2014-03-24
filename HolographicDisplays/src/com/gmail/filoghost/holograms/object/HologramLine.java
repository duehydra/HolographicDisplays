package com.gmail.filoghost.holograms.object;

import org.bukkit.World;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.nms.GenericEntityHologramHorse;
import com.gmail.filoghost.holograms.nms.GenericEntityHologramWitherSkull;

public class HologramLine {
	
	// Only used when spawning entities.
	private static final double VERTICAL_OFFSET = 54.4;

	private GenericEntityHologramHorse horse;
	private GenericEntityHologramWitherSkull witherSkull;
	
	private String text;
	private CraftHologram parent;
	
	public HologramLine(CraftHologram parent, String text) {
		this.parent = parent;
		this.text = text;
	}
	
	public void spawn(World bukkitWorld, double x, double y, double z) throws SpawnFailedException {
		remove();
		
		if (text == null || text.length() == 0) {
			// Do not spawn empty lines
			return;
		}
		
		horse = HolographicDisplays.getNmsManager().spawnHologramHorse(bukkitWorld, x, y + VERTICAL_OFFSET, z, parent);
		witherSkull = HolographicDisplays.getNmsManager().spawnHologramWitherSkull(bukkitWorld, x, y + VERTICAL_OFFSET, z, parent);
		horse.rideSkull(witherSkull); // Let the horse ride the wither skull.
		horse.forceSetCustomName(text); // Other plugins cannot change it.
		horse.setLockTick(true);
		witherSkull.setLockTick(true);
	}
	
	public void remove() {
		if (horse != null) {
			horse.die();
		}
		
		if (witherSkull != null) {
			witherSkull.die();
		}
	}
	
	public String getText() {
		return text;
	}
	
}
