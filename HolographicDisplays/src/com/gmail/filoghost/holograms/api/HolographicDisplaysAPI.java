package com.gmail.filoghost.holograms.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holograms.object.APIHologramManager;
import com.gmail.filoghost.holograms.object.CraftHologram;

public class HolographicDisplaysAPI {

	/**
	 * Create an empty hologram at given location.
	 * This does not return a CraftHologram, but only a Hologram, with less confusing methods.
	 * @return the new hologram created.
	 */
	public static Hologram createHologram(Plugin plugin, Location source, List<String> lines) {
		
		CraftHologram hologram = new CraftHologram("{API-Hologram}", source);
		APIHologramManager.addHologram(plugin, hologram);
		
		if (lines != null && lines.size() > 0) {
			for (String line : lines) {
				hologram.addLine(line);
			}
		}
		
		hologram.update();
		return hologram;
	}
	
	/**
	 * Create an empty hologram at given location.
	 * This does not return a CraftHologram, but only a Hologram, with less confusing methods.
	 */
	public static Hologram createHologram(Plugin plugin, Location source, String... lines) {
		return createHologram(plugin, source, Arrays.asList(lines));
	}
	
	/**
	 * @return all the holograms created with the API by a plugin, never null.
	 */
	public static List<Hologram> getHolograms(Plugin plugin) {
		return APIHologramManager.getHolograms(plugin);
	}
}
