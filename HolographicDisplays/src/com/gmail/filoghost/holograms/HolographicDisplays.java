package com.gmail.filoghost.holograms;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holograms.commands.CommandHandler;
import com.gmail.filoghost.holograms.exception.HologramNotFoundException;
import com.gmail.filoghost.holograms.exception.InvalidLocationException;
import com.gmail.filoghost.holograms.exception.WorldNotFoundException;
import com.gmail.filoghost.holograms.listener.MainListener;
import com.gmail.filoghost.holograms.metrics.MetricsLite;
import com.gmail.filoghost.holograms.nms.interfaces.NmsManager;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.placeholders.PlaceholderManager;
import com.gmail.filoghost.holograms.utils.StringUtils;
import com.gmail.filoghost.holograms.utils.VersionUtils;
import com.gmail.filoghost.holograms.utils.ConfigNode;

public class HolographicDisplays extends JavaPlugin {

	private static Logger logger;
	private static HolographicDisplays instance;
	
	private static double verticalLineSpacing;
	private static String imageSymbol;
	private static String transparencySymbol;

	private static ChatColor transparencyColor;
	
	private static NmsManager nmsManager;
	private CommandHandler commandHandler;
	private static PlaceholderManager placeholderManager;
	
	public void onEnable() {
		instance = this;
		logger = this.getLogger();
		
		loadConfiguration();
		
		String version = VersionUtils.getBukkitVersion();
		
		// It's simple, we don't need reflection.
		if (version.equals("v1_6_R3")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_6_R3.NmsManagerImpl();
		} else if (version.equals("v1_7_R1")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R1.NmsManagerImpl();
		} else if (version.equals("v1_7_R2")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R2.NmsManagerImpl();
		} else if (version.equals("v1_7_R3")) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R3.NmsManagerImpl();
		} else {
			System.out.println(
					 " \n "
					+ "\n***************************************************"
					+ "\n     This version of HolographicDisplays can"
					+ "\n     only work on Bukkit 1.7.X or 1.6.4"
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
	
	public void loadConfiguration() {
		saveDefaultConfig();
		boolean needsSave = false;
		for (ConfigNode node : ConfigNode.values()) {
			if (!getConfig().isSet(node.getPath())) {
				getConfig().set(node.getPath(), node.getDefault());
				needsSave = true;
			}
		}
		if (needsSave) {
			getConfig().options().header(".\n"
									 + ".  Read the tutorial at: http://dev.bukkit.org/bukkit-plugins/holographic-displays/\n"
									 + ".\n"
									 + ".  Plugin created by filoghost.\n"
									 + ".");
			getConfig().options().copyHeader(true);
			saveConfig();
		}
		
		verticalLineSpacing = getConfig().getDouble(ConfigNode.VERTICAL_SPACING.getPath());
		imageSymbol = StringUtils.toReadableFormat(getConfig().getString(ConfigNode.IMAGES_SYMBOL.getPath()));
		transparencySymbol = StringUtils.toReadableFormat(getConfig().getString(ConfigNode.TRANSPARENCY_SPACE.getPath()));
		String tempColor = getConfig().getString(ConfigNode.TRANSPARENCY_COLOR.getPath()).replace("&", "§");
		boolean foundColor = false;
		for (ChatColor chatColor : ChatColor.values()) {
			if (chatColor.toString().equals(tempColor)) {
				transparencyColor = chatColor;
				foundColor = true;
			}
		}
		if (!foundColor) {
			transparencyColor = ChatColor.DARK_GRAY;
			logger.warning("You didn't set a valid chat color for the transparency, dark gray will be used.");
		}
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
	
	public static NmsManager getNmsManager() {
		return nmsManager;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	public static PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public static String getImageSymbol() {
		return imageSymbol;
	}

	public static double getVerticalLineSpacing() {
		return verticalLineSpacing;
	}

	public static String getTransparencySymbol() {
		return transparencySymbol;
	}

	public static ChatColor getTransparencyColor() {
		return transparencyColor;
	}	
}
