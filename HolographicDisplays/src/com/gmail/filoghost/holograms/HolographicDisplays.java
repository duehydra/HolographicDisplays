package com.gmail.filoghost.holograms;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holograms.SimpleUpdater.FailCause;
import com.gmail.filoghost.holograms.bungee.ServerInfoTimer;
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
import com.gmail.filoghost.holograms.placeholders.StaticPlaceholders;
import com.gmail.filoghost.holograms.utils.BungeeCleanupTask;
import com.gmail.filoghost.holograms.utils.StringUtils;
import com.gmail.filoghost.holograms.utils.VersionUtils;
import com.gmail.filoghost.holograms.utils.ConfigNode;
import com.gmail.filoghost.holograms.SimpleUpdater.ResponseHandler;

public class HolographicDisplays extends JavaPlugin {

	private static Logger logger;
	private static HolographicDisplays instance;
	
	private static NmsManager nmsManager;
	private CommandHandler commandHandler;
	private static PlaceholderManager placeholderManager;
	
	public void onEnable() {
		instance = this;
		logger = this.getLogger();
		
		// First of all, load the configuration.
		loadConfiguration();
		
		if (Configuration.updateNotification) {
			new SimpleUpdater(this, 75097, this.getFile()).checkForUpdates(new ResponseHandler() {
				
				@Override
				public void onUpdateFound(final String newVersion) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

						@Override
						public void run() {
							Configuration.newVersion = newVersion;
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
		
		try {
			StaticPlaceholders.load();
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("Unable to read placeholders.yml! Is the file in use?");
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
		ServerInfoTimer.setRefreshSeconds(Configuration.bungeeRefreshSeconds);
		ServerInfoTimer.startTask();
		BungeeCleanupTask.start();
		
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
		
		Configuration.updateNotification = ConfigNode.UPDATE_NOTIFICATION.getBoolean(getConfig());
		Configuration.verticalLineSpacing = ConfigNode.VERTICAL_SPACING.getDouble(getConfig());
		Configuration.imageSymbol = StringUtils.toReadableFormat(ConfigNode.IMAGES_SYMBOL.getString(getConfig()));		
		Configuration.transparencySymbol = StringUtils.toReadableFormat(ConfigNode.TRANSPARENCY_SPACE.getString(getConfig()));
		Configuration.bungeeRefreshSeconds = ConfigNode.BUNGEE_REFRESH_SECONDS.getInt(getConfig());
		
		if (Configuration.bungeeRefreshSeconds < 1) {
			logger.warning("The minimum interval for pinging BungeeCord's servers is 1 second. It has been automatically set.");
			Configuration.bungeeRefreshSeconds = 1;
		}
		
		if (Configuration.bungeeRefreshSeconds > 30) {
			logger.warning("The maximum interval for pinging BungeeCord's servers is 30 seconds. It has been automatically set.");
			Configuration.bungeeRefreshSeconds = 30;
		}
		
		
		String tempColor = ConfigNode.TRANSPARENCY_COLOR.getString(getConfig()).replace("&", "§");
		boolean foundColor = false;
		for (ChatColor chatColor : ChatColor.values()) {
			if (chatColor.toString().equals(tempColor)) {
				Configuration.transparencyColor = chatColor;
				foundColor = true;
			}
		}
		if (!foundColor) {
			Configuration.transparencyColor = ChatColor.GRAY;
			logger.warning("You didn't set a valid chat color for the transparency, light gray will be used.");
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
}
