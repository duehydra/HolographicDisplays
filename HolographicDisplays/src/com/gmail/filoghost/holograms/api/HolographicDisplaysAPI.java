package com.gmail.filoghost.holograms.api;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.object.APIHologramManager;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.utils.GenericUtils;

public class HolographicDisplaysAPI {
	
	/**
	 * Create a hologram at given location.
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
	 * Create a hologram at given location that only a player can see. If the provided player is null, no one will be able to see it.
	 * IMPORTANT NOTE: Requires ProtocolLib.
	 * @return the new hologram created.
	 */
	public static Hologram createIndividualHologram(Plugin plugin, Location source, Player whoCanSee, String... lines) {
		return createIndividualHologram(plugin, source, GenericUtils.createList(whoCanSee), lines);
	}
	
	/**
	 * Create a hologram at given location that only a list of players can see. If the provided list is null, no one will be able to see it.
	 * IMPORTANT NOTE: Requires ProtocolLib.
	 * @return the new hologram created.
	 */
	public static Hologram createIndividualHologram(Plugin plugin, Location source, List<Player> whoCanSee, String... lines) {
		
		CraftHologram hologram = new CraftHologram("{API-Hologram}", source);
		hologram.setUseVisibilityManager(true);
		if (whoCanSee != null) {
			for (Player player : whoCanSee) {
				hologram.getVisibilityManager().showTo(player);
			}
		}
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
