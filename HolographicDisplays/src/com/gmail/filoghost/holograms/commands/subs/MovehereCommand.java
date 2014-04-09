package com.gmail.filoghost.holograms.commands.subs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.filoghost.holograms.commands.CommandValidator;
import com.gmail.filoghost.holograms.commands.HologramSubCommand;
import com.gmail.filoghost.holograms.commands.Messages;
import com.gmail.filoghost.holograms.event.HologramMoveEvent;
import com.gmail.filoghost.holograms.exception.CommandException;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.utils.Format;

public class MovehereCommand extends HologramSubCommand {


	public MovehereCommand() {
		super("movehere");
		setPermission(Messages.MAIN_PERMISSION);
	}

	@Override
	public String getPossibleArguments() {
		return "<hologramName>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}


	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		Player player = CommandValidator.getPlayerSender(sender);
		CraftHologram hologram = HologramManager.getHologram(args[0].toLowerCase());
		CommandValidator.notNull(hologram, Messages.NO_SUCH_HOLOGRAM);
		
		hologram.setLocation(player.getLocation());		
		if (!hologram.update()) {
			player.sendMessage(Messages.FAILED_TO_SPAWN_HERE);
		}
		
		Database.saveHologram(hologram);
		Database.trySaveToDisk();
		Location to = player.getLocation();
		to.setPitch(90);
		player.teleport(to, TeleportCause.PLUGIN);
		player.sendMessage(Format.HIGHLIGHT + "You moved the hologram '" + hologram.getName() + "' near to you.");
		
		// Call the event.
		Bukkit.getPluginManager().callEvent(new HologramMoveEvent(hologram));
	}

	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Moves a hologram to your location.");
	}
	
	@Override
	public SubCommandType getType() {
		return SubCommandType.GENERIC;
	}

}
