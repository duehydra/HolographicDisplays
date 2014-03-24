package com.gmail.filoghost.holograms.commands.subs;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.bobacadodl.imgmessage.ImageMessage;
import com.gmail.filoghost.holograms.Format;
import com.gmail.filoghost.holograms.commands.CommandValidator;
import com.gmail.filoghost.holograms.commands.HologramSubCommand;
import com.gmail.filoghost.holograms.commands.Messages;
import com.gmail.filoghost.holograms.exception.CommandException;
import com.gmail.filoghost.holograms.exception.TooWideException;
import com.gmail.filoghost.holograms.exception.UnreadableImageException;
import com.gmail.filoghost.holograms.object.Database;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramManager;
import com.gmail.filoghost.holograms.utils.FileUtils;
import com.gmail.filoghost.holograms.utils.StringUtils;

public class ReadimageCommand extends HologramSubCommand {


	public ReadimageCommand() {
		super("readimage", "image");
		setPermission(Messages.MAIN_PERMISSION);
	}

	@Override
	public String getPossibleArguments() {
		return "<hologram> <file> <height> [symbol]";
	}

	@Override
	public int getMinimumArguments() {
		return 3;
	}


	@Override
	public void execute(Player sender, String[] args) throws CommandException {
		CraftHologram hologram = HologramManager.getHologram(args[0].toLowerCase());
		CommandValidator.notNull(hologram, Messages.NO_SUCH_HOLOGRAM);
		
		int height = CommandValidator.getInteger(args[2]);
		
		CommandValidator.isTrue(height > 1, "The height of the image must be 2 or greater.");
		CommandValidator.isTrue(height <= 40, "The height cannot be greater than 40.");
		
		String symbol = "\u2588";
		
		if (args.length > 3 && args[3].length() > 0) {
			symbol = args[3];
			symbol = StringUtils.toReadableFormat(symbol);
		}
		
		CommandValidator.isTrue(symbol.length() == 1, "The symbol must be made of a single character (your input was '" + symbol + "' that is " + symbol.length() + " characters long). Note: a placeholder (like '[x]') counts as single character.");
		
		try {
			
			BufferedImage image = FileUtils.readImage(args[1]);
			hologram.clearLines();
			
			ImageMessage imageMessage = new ImageMessage(image, height, symbol);
			String[] newLines = imageMessage.getLines();
			for (int i = 0; i < newLines.length; i++) {
				hologram.addLine(newLines[i]);
			}
			
			if (!hologram.update()) {
				sender.sendMessage(Messages.FAILED_TO_SPAWN_HERE);
			}
			
			Database.saveHologram(hologram);
			Database.trySaveToDisk();
			sender.sendMessage(Format.HIGHLIGHT + "The image was drawn in the hologram!");
			
		} catch (FileNotFoundException e) {
			throw new CommandException("The image '" + args[1] + "' doesn't exist in the plugin's folder.");
		} catch (TooWideException e) {
			throw new CommandException("The image is too large (" + e.getWidth() + " x " + height + "). Max width allowed is 100 pixels, try to decrease the height.");
		} catch (UnreadableImageException e) {
			throw new CommandException("The plugin was unable to read the image. Be sure that the format is supported.");
		} catch (IOException e) {
			throw new CommandException("I/O exception while reading the image. Is it in use?");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommandException("Unhandled exception while reading the image! Please look the console.");
		}
	}
	
	@Override
	public List<String> getTutorial() {
		return Arrays.asList("Reads an image from a file. §fTutorial:",
				"1) Move the image in the plugin's folder",
				"2) Do not use spaces in the name",
				"3) Do /hd read <hologram> <image> <height> [symbol]",
				"4) Choose <height> to automatically resize the image",
				"5) You can use an optional [symbol] instead of the normal '\u2588'",
				"",
				"§fExample: §7you have an image named §f'logo.png'§7, you want",
				"to paste it in the hologram named §f'test'§7, that must be 10",
				"lines high, with this §f'\u2592'§7 symbol. In this case you would",
				"execute §e/hd readimage test logo.png 10 [..]",
				"",
				"§c§lNOTE: §fDo not use big images, as they can cause lag to clients.");
	}
	
	@Override
	public SubCommandType getType() {
		return SubCommandType.EDIT_LINES;
	}

}
