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
import com.gmail.filoghost.holograms.object.HologramLineData;

public class PlaceholderManager {
	
	private static int taskID = -1;
	private static List<HologramLineData> horsesToRefresh;
	private static long elapsedLongTicks;
	
	private static final Pattern BUNGEE_PATTERN = Pattern.compile("(\\{online:)(.+)(\\})");
	private static final Pattern ANIMATION_PATTERN = Pattern.compile("(\\{animation:)(.+)(\\})");
	
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

		// TODO not really safe, could change in the future.
		if (!(customName.contains("{") && customName.contains("}")) && !customName.contains("&u")) {
			// All the placeholders have curly brackets or &u, optimization.
			return;
		}
		
		// Don't create a list if not necessary.
		List<Placeholder> containedPlaceholders = null;
		List<String> bungeeServers = null;
		
		for (Placeholder placeholder : PlaceholdersList.getDefaults()) {
			
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
			customName = customName.replace(matcher.group(), "{online:" + serverName + "}");
			
			// Add it to tracked servers.
			bungeeServers.add(serverName);
		}
		
		// Animation pattern. (Reuse matcher)
		matcher = ANIMATION_PATTERN.matcher(customName);
		while (matcher.find()) {

			String fileName = matcher.group(2).replace(" ", "");
			AnimatedPlaceholder animated = AnimationManager.getFromFilename(fileName);
			
			// If exists...
			if (animated != null) {
				
				customName = customName.replace(matcher.group(), "{animation:" + fileName + "}");
				
				if (containedPlaceholders == null) {
					// Now we create a list, because at least one placeholder has been found.
					containedPlaceholders = new ArrayList<Placeholder>();
				}
				
				containedPlaceholders.add(animated);
			} else {
				
				horse.forceSetCustomName(customName.replace(matcher.group(), "{File not found: " + fileName + "}"));
			}
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
				
				for (Placeholder placeholder : PlaceholdersList.getDefaults()) {
					if (elapsedLongTicks % placeholder.getTenthsToRefresh() == 0) {
						placeholder.update();
					}
				}
				
				for (Placeholder placeholder : PlaceholdersList.getAnimated()) {
					if (elapsedLongTicks % placeholder.getTenthsToRefresh() == 0) {
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
			
		}, 2L, 2L); 
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
				newCustomName = newCustomName.replace("{online:" + server + "}", Integer.toString(ServerInfoTimer.getPlayersOnline(server)));
			}
		}
		
		// Update only if needed, don't send useless packets.
		if (!oldCustomName.equals(newCustomName)) {
			data.getHorse().forceSetCustomName(newCustomName);
		}
	}
}
