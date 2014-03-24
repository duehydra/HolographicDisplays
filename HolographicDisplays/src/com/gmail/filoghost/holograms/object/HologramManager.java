package com.gmail.filoghost.holograms.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;

import com.gmail.filoghost.holograms.HolographicDisplays;

public class HologramManager {

	private static List<CraftHologram> pluginHolograms = new ArrayList<CraftHologram>();
	
	public static void addHologram(CraftHologram hologram) {
		pluginHolograms.add(hologram);
	}
	
	public static List<CraftHologram> getHolograms() {
		return new ArrayList<CraftHologram>(pluginHolograms);
	}
	
	public static CraftHologram getHologram(String name) {
		for (CraftHologram hologram : pluginHolograms) {
			if (hologram.getName().equals(name)) {
				return hologram;
			}
		}
		return null;
	}
	
	public static boolean isExistingHologram(String name) {
		return (getHologram(name) != null);
	}

	public static void onChunkLoad(Chunk chunk) {
		// Fix for server errors.
		HolographicDisplays.getNmsManager().removeWitherSkulls(chunk);
		
		 // Then load the holograms.
		for (CraftHologram hologram : pluginHolograms) {
			if (hologram.isInChunk(chunk)) {
				hologram.forceUpdate();
			}
		}
	}

	public static void remove(CraftHologram hologram) {
		pluginHolograms.remove(hologram);		
	}

	public static int size() {
		return pluginHolograms.size();
	}

	public static CraftHologram get(int i) {
		return pluginHolograms.get(i);
	}
	
	public static void clearAll() {
		for (CraftHologram hologram : pluginHolograms) {
			hologram.hide();
		}
		pluginHolograms.clear();
	}
}
