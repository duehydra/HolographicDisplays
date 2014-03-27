package com.gmail.filoghost.holograms.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holograms.api.Hologram;

public class APIHologramManager {

	private static Map<Plugin, List<Hologram>> apiHolograms = new HashMap<Plugin, List<Hologram>>();
	
	public static void onChunkLoad(Chunk chunk) {		
		for (List<Hologram> pluginHologramList : apiHolograms.values()) {
			for (Hologram hologram : pluginHologramList) {
				if (hologram.isInChunk(chunk)) {
					((CraftHologram) hologram).forceUpdate();
				}
			}
		}
	}
	
	public static void addHologram(Plugin plugin, CraftHologram hologram) {
		List<Hologram> pluginHologramList = apiHolograms.get(plugin);
		if (pluginHologramList == null) {
			pluginHologramList = new ArrayList<Hologram>();
			apiHolograms.put(plugin, pluginHologramList);
		}
		pluginHologramList.add(hologram);
	}
	
	public static void remove(Hologram hologram) {
		hologram.hide();
		for (List<Hologram> pluginHologramList : apiHolograms.values()) {
			pluginHologramList.remove(hologram);
		}
	}
	
	public static List<Hologram> getHolograms(Plugin plugin) {
		List<Hologram> pluginHologramList = apiHolograms.get(plugin);
		if (pluginHologramList == null) {
			return new ArrayList<Hologram>();
		} else {
			return new ArrayList<Hologram>(pluginHologramList);
			// It's a copy of the original list. Holograms should be removed with hologram.delete()
		}
	}
}
