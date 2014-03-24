package com.gmail.filoghost.holograms.commands.subs;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.filoghost.holograms.Format;
import com.gmail.filoghost.holograms.commands.CommandValidator;
import com.gmail.filoghost.holograms.commands.HologramSubCommand;
import com.gmail.filoghost.holograms.commands.Messages;
import com.gmail.filoghost.holograms.exception.CommandException;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;

public class DeleteCommand extends HologramSubCommand {

	public DeleteCommand() {
		super("delete", "remove");
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
		HologramManager.remove(hologram);
		Database.deleteHologram(hologram);
		Database.trySaveToDisk();
		sender.sendMessage(Format.HIGHLIGHT + "You deleted the hologram '" + hologram.getName() + "'.");
	}

	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Deletes a hologram. Cannot be undone.");
	}
	
	@Override
	public SubCommandType getType() {
		return SubCommandType.GENERIC;
	}

}
