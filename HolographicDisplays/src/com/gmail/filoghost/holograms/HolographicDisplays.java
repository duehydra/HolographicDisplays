package com.gmail.filoghost.holograms;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holograms.SimpleUpdater.FailCause;
import com.gmail.filoghost.holograms.commands.CommandHandler;
import com.gmail.filoghost.holograms.exception.HologramNotFoundException;
import com.gmail.filoghost.holograms.exception.InvalidLocationException;
import com.gmail.filoghost.holograms.exception.WorldNotFoundException;
import com.gmail.filoghost.holograms.listener.MainListener;
import com.gmail.filoghost.holograms.metrics.MetricsLite;
import com.gmail.filoghost.holograms.nms.interfaces.NmsManager;
import com.gmail.filoghost.holograms.nms.mcpc.v1_6_R3_MCPCRegistry;
import com.gmail.filoghost.holograms.nms.mcpc.v1_7_R1_MCPCRegistry;
import com.gmail.filoghost.holograms.nms.mcpc.v1_7_R2_MCPCRegistry;
import com.gmail.filoghost.holograms.nms.mcpc.v1_7_R3_MCPCRegistry;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.placeholders.PlaceholderManager;
import com.gmail.filoghost.holograms.utils.StringUtils;
import com.gmail.filoghost.holograms.utils.VersionUtils;
import com.gmail.filoghost.holograms.utils.ConfigNode;
import com.gmail.filoghost.holograms.SimpleUpdater.ResponseHandler;

public class HolographicDisplays extends JavaPlugin {

	private static Logger logger;
	private static HolographicDisplays instance;
	
	private static double verticalLineSpacing;
	private static String imageSymbol;
	private static String transparencySymbol;
	private static boolean updateNotification;
	
	private static String newVersion;

	private static ChatColor transparencyColor;
	
	private static NmsManager nmsManager;
	private CommandHandler commandHandler;
	private static PlaceholderManager placeholderManager;
	
	public void onEnable() {
		instance = this;
		logger = this.getLogger();
		
		loadConfiguration();
		if (updateNotification) {
			new SimpleUpdater(this, 75097, this.getFile()).checkForUpdates(new ResponseHandler() {
				
				@Override
				public void onUpdateFound(final String newVersion) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

						@Override
						public void run() {
							HolographicDisplays.newVersion = newVersion;
							getLogger().info("Found a new version available: " + newVersion);
							getLogger().info("Download it on Bukkit Dev:");
							getLogger().info("dev.bukkit.org/bukkit-plugins/holographic-displays");
						}
						
					});
					
				}
				
				@Override
				public void onFail(FailCause result) {
					// Handle BAD_VERSION and INVALID_PROJECT_ID only.
					if (result == FailCause.BAD_VERSION) {
						getLogger().warning("The author of this plugin has misconfigured the Updater system.");
						getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
			            getLogger().warning("Please notify the author of this error.");
					} else if (result == FailCause.INVALID_PROJECT_ID) {
						getLogger().warning("The author of this plugin has misconfigured the Updater system.");
						getLogger().warning("The project ID (" + 75097 + ") provided for updating is invalid.");
						getLogger().warning("Please notify the author of this error.");
					} else if (result == FailCause.BUKKIT_OFFLINE) {
						getLogger().warning("Could not contact BukkitDev to check for updates.");
					}
				}
			});
		}
		
		String version = VersionUtils.getBukkitVersion();
		
		if (version == null) {
			// Caused by MCPC+ renaming packages, get the version from Bukkit.getVersion()
			version = VersionUtils.getMinecraftVersion();
			
			if ("1.6.4".equals(version)) {
				version = "v1_6_R3";
			} else if ("1.7.2".equals(version)) {
				version = "v1_7_R1";
			} else if ("1.7.5".equals(version)) {
				version = "v1_7_R2";
			} else if ("1.7.8".equals(version)) {
				version = "v1_7_R3";
			} else {
				// Cannot definitely get the version. This will cause HD to disable itself.
				version = null;
			}
		}
		
		// It's simple, we don't need reflection.
		if ("v1_6_R3".equals(version)) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_6_R3.NmsManagerImpl();
		} else if ("v1_7_R1".equals(version)) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R1.NmsManagerImpl();
		} else if ("v1_7_R2".equals(version)) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R2.NmsManagerImpl();
		} else if ("v1_7_R3".equals(version)) {
			nmsManager = new com.gmail.filoghost.holograms.nms.v1_7_R3.NmsManagerImpl();
		} else {
			printWarnAndDisable(
				"******************************************************",
				"     This version of HolographicDisplays can",
				"     only work on these server versions:",
				"     1.6.4, from 1.7.2 to 1.7.8.",
				"     The plugin will be disabled.",
				"******************************************************"
			);
			return;
		}
		
		try {
			if (Bukkit.getVersion().contains("MCPC-Plus")) {
				getLogger().info("Trying to enable MCPC+ support...");
				if (version.equals("v1_6_R3")) {
					v1_6_R3_MCPCRegistry.registerCustomEntity(nmsManager.getHologramHorseClass(), "EntityHorse", 100);
					v1_6_R3_MCPCRegistry.registerCustomEntity(nmsManager.getHologramWitherSkullClass(), "WitherSkull", 19);
					getLogger().info("Successfully registered entities for MCPC+!");
				} else if (version.equals("v1_7_R1")) {
					v1_7_R1_MCPCRegistry.registerCustomEntity(nmsManager.getHologramHorseClass(), "EntityHorse", 100);
					v1_7_R1_MCPCRegistry.registerCustomEntity(nmsManager.getHologramWitherSkullClass(), "WitherSkull", 19);
					getLogger().info("Successfully registered entities for MCPC+!");
				} else if (version.equals("v1_7_R2")) {
					v1_7_R2_MCPCRegistry.registerCustomEntity(nmsManager.getHologramHorseClass(), "EntityHorse", 100);
					v1_7_R2_MCPCRegistry.registerCustomEntity(nmsManager.getHologramWitherSkullClass(), "WitherSkull", 19);
					getLogger().info("Successfully registered entities for MCPC+!");
				} else if (version.equals("v1_7_R3")) {
					v1_7_R3_MCPCRegistry.registerCustomEntity(nmsManager.getHologramHorseClass(), "EntityHorse", 100);
					v1_7_R3_MCPCRegistry.registerCustomEntity(nmsManager.getHologramWitherSkullClass(), "WitherSkull", 19);
					getLogger().info("Successfully registered entities for MCPC+!");
				} else {
					printWarnAndDisable(
						"******************************************************",
						"     This version of MCPC+ is not supported yet.",
						"     Supported versions are 1.7.2 and 1.6.4.",
						"     The plugin will be disabled.",
						"******************************************************"
					);
					return;
				}
			} else {
				nmsManager.registerCustomEntities();
			}
		} catch (Exception e) {
			e.printStackTrace();
			printWarnAndDisable(
				"******************************************************",
				"     HolographicDisplays was unable to register",
				"     custom entities, the plugin will be disabled.",
				"     Are you using the correct Bukkit version?",
				"******************************************************"
			);
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
		
		updateNotification = getConfig().getBoolean(ConfigNode.UPDATE_NOTIFICATION.getPath());
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
	
	private static void printWarnAndDisable(String... messages) {
		StringBuffer buffer = new StringBuffer("\n ");
		for (String message : messages) {
			buffer.append('\n');
			buffer.append(message);
		}
		buffer.append('\n');
		System.out.println(buffer.toString());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) { }
		instance.setEnabled(false);
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
	
	public static boolean updateNotification() {
		return updateNotification;
	}
	
	public static String getNewVersion() {
		return newVersion;
	}
}
