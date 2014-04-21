package com.gmail.filoghost.holograms.placeholders;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.utils.FileUtils;
import com.gmail.filoghost.holograms.utils.AnimationData;
import com.gmail.filoghost.holograms.utils.StringUtils;

public class AnimationManager {
	
	// <{a:fileName}, lines>
	private static Map<String, AnimationData> files = new HashMap<String, AnimationData>();
	
	public static void loadAnimations() {
		files.clear();
		PlaceholdersList.clearAnimated();
		
		File animationFolder = new File(HolographicDisplays.getInstance().getDataFolder(), "animations");
		if (!animationFolder.exists() || !animationFolder.isDirectory()) {
			HolographicDisplays.getInstance().saveResource("animations/example.txt", false);
			return;
		}
		
		for (File file : animationFolder.listFiles()) {
			
			try {
				List<String> lines = FileUtils.readLines(file);
				if (lines.size() == 0) {
					continue;
				}
				
				int speed = 2;
				boolean validSpeedFound = false;
				
				String firstLine = lines.get(0).trim();
				if (firstLine.toLowerCase().startsWith("speed:")) {
					
					// Do not consider it.
					lines.remove(0);
					
					firstLine = firstLine.substring("speed:".length()).trim();
					
					try {
						int tempSpeed = Integer.parseInt(firstLine);
						if (tempSpeed > 0) {
							
							// Ok, it's valid.
							speed = tempSpeed;
							validSpeedFound = true;

						}
					} catch (NumberFormatException e) {
					}
				}
				
				if (!validSpeedFound) {
					HolographicDisplays.getInstance().getLogger().warning("Could not find 'speed: <number>' in the file '" + file.getName() + "'. Default speed of 2 will be used.");
				}
				
				if (lines.size() == 0) {
					lines.add("{No lines: " + file.getName() + "}");
					HolographicDisplays.getInstance().getLogger().warning("Could not find any line in '" + file.getName() + "' except for the speed. You should add at least one more line.");
				}
				
				// Replace placeholders.
				for (int i = 0; i < lines.size(); i++) {
					lines.set(i, StringUtils.toReadableFormat(lines.get(i)));
				}
				
				files.put("{animation:" + file.getName() + "}", new AnimationData(lines, speed));
				HolographicDisplays.getInstance().getLogger().info("Loaded animation '"  + file.getName() + "', speed " + speed + ".");
				
			} catch (Exception e) {
				e.printStackTrace();
				HolographicDisplays.getInstance().getLogger().severe("Couldn't load the file '" + file.getName() + "'!");
			}
			
		}
	}
	
	// Load them dynamically and only if needed.
	public static AnimatedPlaceholder getFromFilename(String filename) {
		
		String placeholderString = "{animation:" + filename + "}";
		
		for (AnimatedPlaceholder existing : PlaceholdersList.getAnimated()) {
			if (existing.getLongPlaceholder().equals(placeholderString)) {
				return existing;
			}
		}

		if (files.containsKey(placeholderString)) {
			AnimationData data = files.get(placeholderString);
			AnimatedPlaceholder animated = new AnimatedPlaceholder(placeholderString, data.getSpeed(), data.getLines());
			PlaceholdersList.addAnimatedPlaceholder(animated);
			return animated;
		}
		
		// Not found & not created
		return null;
	}
	
}
