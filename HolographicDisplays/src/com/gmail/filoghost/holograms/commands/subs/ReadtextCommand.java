package com.gmail.filoghost.holograms.commands.subs;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.gmail.filoghost.holograms.utils.FileUtils;
import com.gmail.filoghost.holograms.utils.StringUtils;

public class ReadtextCommand extends HologramSubCommand {

	public ReadtextCommand() {
		super("readtext", "readlines");
		setPermission(Messages.MAIN_PERMISSION);
	}

	@Override
	public String getPossibleArguments() {
		return "<hologramName> <fileWithExtension>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public void execute(Player sender, String[] args) throws CommandException {
		CraftHologram hologram = HologramManager.getHologram(args[0].toLowerCase());
		CommandValidator.notNull(hologram, Messages.NO_SUCH_HOLOGRAM);
		
		try {
			List<String> lines = FileUtils.readLines(args[1]);
			hologram.clearLines();
			
			int linesAmount = lines.size();
			if (linesAmount > 40) {
				sender.sendMessage("§eThe file contained more than 40 lines, that have been limited.");
				linesAmount = 40;
			}
			
			for (int i = 0; i < linesAmount; i++) {
				hologram.addLine(StringUtils.toReadableFormat(lines.get(i)));
			}
			
			if (!hologram.update()) {
				sender.sendMessage(Messages.FAILED_TO_SPAWN_HERE);
			}

			Database.saveHologram(hologram);
			Database.trySaveToDisk();
			sender.sendMessage(Format.HIGHLIGHT + "The lines were pasted into the hologram!");
			
		} catch (FileNotFoundException e) {
			throw new CommandException("A file named '" + args[1] + "' doesn't exist in the plugin's folder.");
		} catch (IOException e) {
			throw new CommandException("I/O exception while reading the file. Is it in use?");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommandException("Unhandled exception while reading the file! Please look the console.");
		}
	}
	
	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Reads the lines from a text file. §fTutorial:",
			"1) Create a new text file in the plugin's folder",
			"2) Do not use spaces in the name",
			"3) Each line will be a line in the hologram",
			"4) Do /hd readlines <hologramName> <fileWithExtension>",
			"",
			"§fExample: §7you have a file named §f'info.txt'§7, and you want",
			"to paste it in the hologram named §f'test'§7. In this case you",
			"would execute §e/hd readlines test info.txt");
	}
	
	@Override
	public SubCommandType getType() {
		return SubCommandType.EDIT_LINES;
	}

}
