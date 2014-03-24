package com.gmail.filoghost.holograms.commands.subs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.filoghost.holograms.Format;
import com.gmail.filoghost.holograms.commands.CommandValidator;
import com.gmail.filoghost.holograms.commands.HologramSubCommand;
import com.gmail.filoghost.holograms.commands.Messages;
import com.gmail.filoghost.holograms.exception.CommandException;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;

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
	public void execute(Player sender, String[] args) throws CommandException {
		CraftHologram hologram = HologramManager.getHologram(args[0].toLowerCase());
		CommandValidator.notNull(hologram, Messages.NO_SUCH_HOLOGRAM);
		
		hologram.hide();
		hologram.setLocation(sender.getLocation());
		
		if (!hologram.update()) {
			sender.sendMessage(Messages.FAILED_TO_SPAWN_HERE);
		}
		
		Database.saveHologram(hologram);
		Database.trySaveToDisk();
		Location to = sender.getLocation();
		to.setPitch(90);
		sender.teleport(to, TeleportCause.PLUGIN);
		sender.sendMessage(Format.HIGHLIGHT + "You moved the hologram '" + hologram.getName() + "' near to you.");
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
