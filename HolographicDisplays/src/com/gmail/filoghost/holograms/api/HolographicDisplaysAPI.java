package com.gmail.filoghost.holograms.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.object.APIHologramManager;
import com.gmail.filoghost.holograms.object.CraftHologram;

public class HolographicDisplaysAPI {
	
	/**
	 * Create an empty hologram at given location.
	 * This does not return a CraftHologram, but only a Hologram, with less confusing methods.
	 * @return the new hologram created.
	 */
	public static Hologram createHologram(Plugin plugin, Location source, String... lines) {
		
		CraftHologram hologram = new CraftHologram("{API-Hologram}", source);
		APIHologramManager.addHologram(plugin, hologram);
		
		if (lines != null && lines.length > 0) {
			for (String line : lines) {
				hologram.addLine(line);
			}
		}
		
		hologram.update();
		return hologram;
	}
	
	/**
	 * @return a copy of all the holograms created with the API by a plugin.
	 */
	public static Hologram[] getHolograms(Plugin plugin) {
		return APIHologramManager.getHolograms(plugin);
	}
	
	/**
	 * @return if the entity is part of a hologram.
	 */
	public static boolean isHologramEntity(Entity bukkitEntity) {
		return HolographicDisplays.getNmsManager().isHologramEntity(bukkitEntity);
	}
}
