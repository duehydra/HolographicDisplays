package com.gmail.filoghost.holograms.nms;

import org.bukkit.Chunk;

import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.object.CraftHologram;

public interface GenericNmsManager {
	
	public void registerCustomEntities() throws Exception;
	
	public GenericEntityHologramHorse spawnHologramHorse(org.bukkit.World world, double x, double y, double z, CraftHologram parent) throws SpawnFailedException;
	
	public GenericEntityHologramWitherSkull spawnHologramWitherSkull(org.bukkit.World bukkitWorld, double x, double y, double z, CraftHologram parent) throws SpawnFailedException;

	public void removeWitherSkulls(Chunk chunk);
	
	public boolean isHologramEntity(org.bukkit.entity.Entity bukkitEntity);
	
	// Return null if not a hologram's entity.
	public CraftHologram getHologram(org.bukkit.entity.Entity bukkitEntity);
	
	public GenericFancyMessage newFancyMessage(String text);
}
