package com.gmail.filoghost.holograms.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.gmail.filoghost.holograms.nms.GenericNmsManager;
import com.gmail.filoghost.holograms.object.APIHologramManager;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;

public class MainListener implements Listener {
	
	private GenericNmsManager nmsManager;
	
	public MainListener(GenericNmsManager nmsManager) {
		this.nmsManager = nmsManager;
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {		
			if (!entity.isDead()) {
				CraftHologram hologram = nmsManager.getHologram(entity);
				
				if (hologram != null) {
					hologram.hide();
				}
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		HologramManager.onChunkLoad(event.getChunk());
		APIHologramManager.onChunkLoad(event.getChunk());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (nmsManager.isHologramEntity(event.getEntity())) {
			if (event.isCancelled()) {
				event.setCancelled(false);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (nmsManager.isHologramEntity(event.getEntity())) {
			if (event.isCancelled()) {
				event.setCancelled(false);
			}
		}
	}
}
