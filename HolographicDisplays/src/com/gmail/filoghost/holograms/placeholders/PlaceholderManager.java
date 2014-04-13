package com.gmail.filoghost.holograms.placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.utils.EntityAndNamePair;

public class PlaceholderManager {
	
	private static Placeholder[] registeredPlaceholders;
	private static int taskId = -1;
	private static List<EntityAndNamePair> horsesToRefresh;
	private static long elapsedLongTicks;
	
	public PlaceholderManager() {
		horsesToRefresh = new ArrayList<EntityAndNamePair>();
		
		registeredPlaceholders = new Placeholder[] {
			new RainbowText(),
			new OnlinePlayers(),
			new MaxPlayers()
		};
		
		// Start the repeating tasks.
		startTask();
	}
	
	public void trackIfNecessary(HologramHorse horse) {
		
		boolean containsAnyPlaceholder = false;
		String customName = horse.getEntityCustomName();
		
		if (customName == null || customName.length() == 0) {
			return;
		}
		
		for (Placeholder placeholder : registeredPlaceholders) {			
			if (customName.contains(placeholder.getLongPlaceholder())) {
				containsAnyPlaceholder = true;
				// Optimize calculations with shorter placeholders.
				customName = customName.replace(placeholder.getLongPlaceholder(), placeholder.getShortPlaceholder());
			}
		}
		
		if (containsAnyPlaceholder) {
			horsesToRefresh.add(new EntityAndNamePair(horse, customName));
			updatePlaceholders(horse, customName);
			
		}
	}
	
	public void startTask() {
		
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
		}
		
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(HolographicDisplays.getInstance(), new Runnable() {			
			
			public void run() {
				
				for (Placeholder placeholder : registeredPlaceholders) {
					
					if (elapsedLongTicks % placeholder.getRefreshTicks() == 0) {
						placeholder.update();
					}

				}
				
				Iterator<EntityAndNamePair> iter = horsesToRefresh.iterator();
				EntityAndNamePair pair;
				
				while (iter.hasNext()) {
					pair = iter.next();
					
					if (pair.getHorse().isDead()) {
						iter.remove();
					} else {
						updatePlaceholders(pair.getHorse(), pair.getSavedName());
					}
				}
				
				elapsedLongTicks++;
			}
			
		}, 4L, 4L);
	}
	
	private void updatePlaceholders(HologramHorse horse, String newCustomName) {
		
		String oldCustomName = horse.getEntityCustomName();
		
		for (Placeholder placeholder : registeredPlaceholders) {
			newCustomName = newCustomName.replace(placeholder.getShortPlaceholder(), placeholder.getReplacement());
		}
		
		// Update only if needed, don't send useless packets.
		if (!oldCustomName.equals(newCustomName)) {
			horse.forceSetCustomName(newCustomName);
		}
	}

}
