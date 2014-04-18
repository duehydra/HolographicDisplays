package com.gmail.filoghost.holograms.placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.bungee.ServerInfoTimer;
import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.utils.HologramLineData;

public class PlaceholderManager {
	
	private static int taskID = -1;
	private static List<HologramLineData> horsesToRefresh;
	private static long elapsedLongTicks;
	
	private static final Pattern BUNGEE_PATTERN = Pattern.compile("(\\{bungee:|\\{online:|\\{server:)(.+)(\\})");
	
	public PlaceholderManager() {
		horsesToRefresh = new ArrayList<HologramLineData>();
		
		// Start the repeating tasks.
		startTask();
	}
	
	public void trackIfNecessary(HologramHorse horse) {
		
		String customName = horse.getEntityCustomName();
		if (customName == null || customName.length() == 0) {
			return;
		}

		if (!(customName.contains("{") && customName.contains("}")) && !customName.contains("&u")) {
			// All the placeholders have curly brackets or &u, optimization.
			return;
		}
		
		// Don't create a list if not necessary.
		List<Placeholder> containedPlaceholders = null;
		List<String> bungeeServers = null;
		
		for (Placeholder placeholder : Placeholder.values()) {
			
			if (customName.contains(placeholder.getLongPlaceholder())) {
				
				if (containedPlaceholders == null) {
					// Now we create a list, because at least one placeholder has been found.
					containedPlaceholders = new ArrayList<Placeholder>();
				}
				
				// Optimize calculations with shorter placeholders.
				customName = customName.replace(placeholder.getLongPlaceholder(), placeholder.getShortPlaceholder());
				
				// Add the placeholder to the list.
				containedPlaceholders.add(placeholder);
			}
			
		}
		
		// BungeeCord pattern.
		Matcher matcher = BUNGEE_PATTERN.matcher(customName);
		while (matcher.find()) {
			
			if (bungeeServers == null) {
				bungeeServers = new ArrayList<String>();
			}
			
			String serverName = matcher.group(2).replace(" ", "");
			ServerInfoTimer.track(serverName); // Track this server.
			
			// Shorter placeholder without spaces.
			customName = customName.replace(matcher.group(), "{b:" + serverName + "}");
			
			// Add it to tracked servers.
			bungeeServers.add(serverName);
		}
		
		
		if (containedPlaceholders != null || bungeeServers != null) {
			HologramLineData data = new HologramLineData(horse, customName);
			if (containedPlaceholders != null) {
				data.setContainedPlaceholders(containedPlaceholders);
			}
			if (bungeeServers != null) {
				data.setContainedBungeeServers(bungeeServers);
			}
			
			horsesToRefresh.add(data);
			
			updatePlaceholders(data);
		}
	}
	
	public void startTask() {
		
		if (taskID != -1) {
			Bukkit.getScheduler().cancelTask(taskID);
		}
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HolographicDisplays.getInstance(), new Runnable() {			
			
			public void run() {
				
				for (Placeholder placeholder : Placeholder.values()) {
					
					if (elapsedLongTicks % placeholder.getLongRefreshTicks() == 0) {
						placeholder.update();
					}

				}
				
				Iterator<HologramLineData> iter = horsesToRefresh.iterator();
				
				HologramLineData current;
				
				while (iter.hasNext()) {
					current = iter.next();
					
					if (current.getHorse().isDead()) {
						iter.remove();
					} else {
						updatePlaceholders(current);
					}
				}
				
				elapsedLongTicks++;
			}
			
		}, 4L, 4L); 
	}
	
	private void updatePlaceholders(HologramLineData data) {
		
		String oldCustomName = data.getHorse().getEntityCustomName();
		String newCustomName = data.getSavedName();
		
		if (data.hasPlaceholders()) {
			for (Placeholder placeholder : data.getPlaceholders()) {
				newCustomName = newCustomName.replace(placeholder.getShortPlaceholder(), placeholder.getReplacement());
			}
		}
		
		if (data.hasBungeeServers()) {
			for (String server : data.getBungeeServers()) {
				newCustomName = newCustomName.replace("{b:" + server + "}", Integer.toString(ServerInfoTimer.getPlayersOnline(server)));
			}
		}
		
		// Update only if needed, don't send useless packets.
		if (!oldCustomName.equals(newCustomName)) {
			data.getHorse().forceSetCustomName(newCustomName);
		}
	}
}
