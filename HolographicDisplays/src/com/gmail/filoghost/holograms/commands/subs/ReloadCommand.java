package com.gmail.filoghost.holograms.commands.subs;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.gmail.filoghost.holograms.Configuration;
import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.bungee.ServerInfoTimer;
import com.gmail.filoghost.holograms.commands.HologramSubCommand;
import com.gmail.filoghost.holograms.commands.Messages;
import com.gmail.filoghost.holograms.exception.CommandException;
import com.gmail.filoghost.holograms.exception.HologramNotFoundException;
import com.gmail.filoghost.holograms.exception.InvalidLocationException;
import com.gmail.filoghost.holograms.exception.WorldNotFoundException;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.placeholders.StaticPlaceholders;

public class ReloadCommand extends HologramSubCommand {

	public ReloadCommand() {
		super("reload");
		setPermission(Messages.MAIN_PERMISSION);
	}

	@Override
	public String getPossibleArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		try {
			
			long startMillis = System.currentTimeMillis();
			
			HolographicDisplays.getInstance().reloadConfig();
			HolographicDisplays.getInstance().loadConfiguration();
			
			ServerInfoTimer.setRefreshSeconds(Configuration.bungeeRefreshSeconds);
			ServerInfoTimer.startTask();
			
			StaticPlaceholders.load();
			
			Database.initialize();
			HologramManager.clearAll();

			Set<String> savedHolograms = Database.getHolograms();
			if (savedHolograms != null && savedHolograms.size() > 0) {
				for (String singleSavedHologram : savedHolograms) {
					try {
						CraftHologram singleHologramEntity = Database.loadHologram(singleSavedHologram);
						HologramManager.addHologram(singleHologramEntity);
					} catch (HologramNotFoundException e) {
						sender.sendMessage("§c[ ! ] §7Hologram '" + singleSavedHologram + "' not found, skipping it.");
					} catch (InvalidLocationException e) {
						sender.sendMessage("§c[ ! ] §7Hologram '" + singleSavedHologram + "' has an invalid location format.");
					} catch (WorldNotFoundException e) {
						sender.sendMessage("§c[ ! ] §7Hologram '" + singleSavedHologram + "' was in the world '" + e.getMessage() + "' but it wasn't loaded.");
					}
				}
			}
			
			for (CraftHologram hologram : HologramManager.getHolograms()) {
				if (!hologram.update()) {
					sender.sendMessage("§c[ ! ] §7Unable to spawn entities for the hologram '" + hologram.getName() + "'.");
				}
			}
			
			long endMillis = System.currentTimeMillis();
			
			sender.sendMessage("§bConfiguration reloaded successfully in " + (endMillis - startMillis) + "ms!");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CommandException("Exception while reloading the configuration. Please look the console.");
		}
	}
	
	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Reloads the holograms from the database.");
	}
	
	@Override
	public SubCommandType getType() {
		return SubCommandType.GENERIC;
	}

}
