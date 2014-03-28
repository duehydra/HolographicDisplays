package com.gmail.filoghost.holograms;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holograms.commands.CommandHandler;
import com.gmail.filoghost.holograms.exception.HologramNotFoundException;
import com.gmail.filoghost.holograms.exception.InvalidLocationException;
import com.gmail.filoghost.holograms.exception.WorldNotFoundException;
import com.gmail.filoghost.holograms.listener.MainListener;
import com.gmail.filoghost.holograms.metrics.MetricsLite;
import com.gmail.filoghost.holograms.nms.GenericNmsManager;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.placeholders.PlaceholderManager;
import com.gmail.filoghost.holograms.utils.VersionUtils;

public class HolographicDisplays extends JavaPlugin {

	private static Logger logger;
	private static HolographicDisplays instance;
	
	private static double verticalLineSpacing = 0.25;
	
	private static GenericNmsManager nmsManager;
	private CommandHandler commandHandler;
	private static PlaceholderManager placeholderManager;
	
	public void onEnable() {
		instance = this;
		logger = this.getLogger();
		
		saveDefaultConfig();
		verticalLineSpacing = getConfig().getDouble("vertical-spacing", 0.25);
		
		String version = VersionUtils.getBukkitVersion();
		
		// It's simple, we don't need reflection
		if (version.equals("v1_7_R1")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R1.NmsManager();
		} else if (version.equals("v1_7_R2")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R2.NmsManager();
		} else {
			System.out.println(
					 " \n "
					+ "\n***************************************************"
					+ "\n     This version of HolographicDisplays can"
					+ "\n     only work on Bukkit 1.7.*"
					+ "\n     The plugin will be disabled."
			 		+ "\n***************************************************"
			 		+ "\n ");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		try {
			nmsManager.registerCustomEntities();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					 " \n "
					+ "\n***************************************************"
					+ "\n     HolographicDisplays was unable to register"
					+ "\n     custom entities, the plugin will be disabled."
					+ "\n     Are you using the correct Bukkit version?"
			 		+ "\n***************************************************"
			 		+ "\n ");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ex) {
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		placeholderManager = new PlaceholderManager();
		
		// Initalize static classes.
		Database.initialize();
		
		Set<String> savedHolograms = Database.getHolograms();
		if (savedHolograms != null && savedHolograms.size() > 0) {
			for (String singleSavedHologram : savedHolograms) {
				try {
					CraftHologram singleHologramEntity = Database.loadHologram(singleSavedHologram);
					HologramManager.addHologram(singleHologramEntity);
				} catch (HologramNotFoundException e) {
					logger.warning("Hologram '" + singleSavedHologram + "' not found, skipping it.");
				} catch (InvalidLocationException e) {
					logger.warning("Hologram '" + singleSavedHologram + "' has an invalid location format.");
				} catch (WorldNotFoundException e) {
					logger.warning("Hologram '" + singleSavedHologram + "' was in the world '" + e.getMessage() + "' but it wasn't loaded.");
				} catch (Exception e) {
					e.printStackTrace();
					logger.warning("Unhandled exception while loading '" + singleSavedHologram + "'. Please contact the developer.");
				}
			}
		}
		
		getCommand("holograms").setExecutor(commandHandler = new CommandHandler());
		Bukkit.getPluginManager().registerEvents(new MainListener(nmsManager), this);
		
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (Exception ignore) { }
		
		// The entities are loaded when the server is ready.
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				
				// Temp fix.				
				for (World world : Bukkit.getWorlds()) {
					for (Chunk chunk : world.getLoadedChunks()) {
						nmsManager.removeWitherSkulls(chunk);
					}
				}
				
				
				for (CraftHologram hologram : HologramManager.getHolograms()) {
					if (!hologram.update()) {
						logger.warning("Unable to spawn entities for the hologram '" + hologram.getName() + "'.");
					}
				}				
			}
		}, 10L);
	}
	
	public void onDisable() {
		for (CraftHologram hologram : HologramManager.getHolograms()) {
			hologram.hide();
		}

		try {
			Database.saveToDisk();
			logger.info("Holograms saved!");
		} catch (IOException e) {
			e.printStackTrace();
			logger.severe("Unable to save holograms to database.yml! Was the file in use?");
		}
	}

	public static HolographicDisplays getInstance() {
		return instance;
	}
	
	public static GenericNmsManager getNmsManager() {
		return nmsManager;
	}

	public static double getVerticalLineSpacing() {
		return verticalLineSpacing;
	}
	
	public static void setVerticalLineSpacing(double newValue) {
		verticalLineSpacing = newValue;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	public static PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}	
}
