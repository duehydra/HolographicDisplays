package com.gmail.filoghost.holograms.nms.interfaces;

import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.object.CraftHologram;

public interface NmsManager {
	
	public void registerCustomEntities() throws Exception;
	
	public HologramHorse spawnHologramHorse(org.bukkit.World world, double x, double y, double z, CraftHologram parent) throws SpawnFailedException;
	
	public HologramWitherSkull spawnHologramWitherSkull(org.bukkit.World bukkitWorld, double x, double y, double z, CraftHologram parent) throws SpawnFailedException;
	
	public boolean isHologramEntity(org.bukkit.entity.Entity bukkitEntity);
	
	// Return null if not a hologram's entity.
	public CraftHologram getHologram(org.bukkit.entity.Entity bukkitEntity);
	
	public FancyMessage newFancyMessage(String text);

	public boolean hasChatHoverFeature();
	
	// Used for MCPC.
	public Class<?> getHologramHorseClass();
	public Class<?> getHologramWitherSkullClass();
}
