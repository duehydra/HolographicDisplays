package com.gmail.filoghost.holograms.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.exception.HologramDeletedException;
import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.nms.interfaces.HologramWitherSkull;

/**
 * This class is only used by the plugin itself. Other plugins should just use the API.
 */

public class CraftHologram extends Hologram {

	private static double VERTICAL_OFFSET = 54.313;
	
	private List<HologramHorse> horses;
	private List<HologramWitherSkull> witherSkulls;
	
	private List<String> lines;	
	private int customNameLimit;
	
	private String name;
	private World bukkitWorld;
	private double x;
	private double y;
	private double z;
	
	private int chunkX;
	private int chunkZ;
	
	public CraftHologram(String name, Location source) {
		this.name = name;
		bukkitWorld = source.getWorld();
		x = source.getX();
		y = source.getY();
		z = source.getZ();
		chunkX = source.getChunk().getX();
		chunkZ = source.getChunk().getZ();
		lines = new ArrayList<String>();
		horses = new ArrayList<HologramHorse>();
		witherSkulls = new ArrayList<HologramWitherSkull>();
		customNameLimit = HolographicDisplays.getNmsManager().getCustomNameLimit();
	}
	
	public void setLocation(Location source) {
		
		if (source == null) {
			throw new NullPointerException("Source location of the hologram cannot be null");
		}
		if (source.getWorld() == null) {
			throw new NullPointerException("World of the source location of the hologram cannot be null");
		}
		
		bukkitWorld = source.getWorld();
		x = source.getX();
		y = source.getY();
		z = source.getZ();
		chunkX = source.getChunk().getX();
		chunkZ = source.getChunk().getZ();
	}
	
	public Location getLocation() {
		return new Location(bukkitWorld, x, y, z);
	}
	
	public String getName() {
		return name;
	}
	
	public int getBlockX() {
		return (int) x;
	}
	
	public int getBlockY() {
		return (int) y;
	}
	
	public int getBlockZ() {
		return (int) z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}

	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkZ() {
		return chunkZ;
	}
	
	public boolean isInChunk(Chunk chunk) {
		return chunk.getX() == chunkX && chunk.getZ() == chunkZ;
	}
	
	public boolean isInLoadedChunk() {
		return bukkitWorld.isChunkLoaded(chunkX, chunkZ);
	}
	
	public World getWorld() {
		return bukkitWorld;
	}
	
	public String getWorldName() {
		return bukkitWorld.getName();
	}
	
	public void addLine(String message) {
		if (message == null) {
			message = "";
		}
		
		if (message.length() > customNameLimit) {
			message = message.substring(0, customNameLimit);
		}
		
		lines.add(message);
	}
	
	public void insertLine(int index, String message) {
		if (message == null) {
			message = "";
		}
		
		if (message.length() > customNameLimit) {
			message = message.substring(0, customNameLimit);
		}
		
		lines.add(index, message);
	}

	public String[] getLines() {
		return lines.toArray(new String[lines.size()]);
	}
	
	public void setLine(int index, String text) {
		if (text == null) {
			text = "";
		}
		
		if (text.length() > customNameLimit) {
			text = text.substring(0, customNameLimit);
		}
		
		lines.set(index, text);
	}
	
	public void clearLines() {
		lines.clear();
	}
	
	public void removeLine(int index) {
		lines.remove(index);
	}
	
	public int getLinesLength() {
		return lines.size();
	}
	
	public boolean update() {
		if (isInLoadedChunk()) {
			return forceUpdate();
		}
		
		return true;
	}
	
	/**
	 *  Updates the hologram without checking for a loaded chunk.
	 */
	public boolean forceUpdate() {
		
		if (deleted) {
			throw new HologramDeletedException("Hologram already deleted!");
		}
		
		// Remove previous entities.
		hide();
		
		try {
			
			double lineSpacing = HolographicDisplays.getVerticalLineSpacing();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				HologramHorse horse = HolographicDisplays.getNmsManager().spawnHologramHorse(bukkitWorld, x, y + VERTICAL_OFFSET - (i*lineSpacing), z, this);
				horses.add(horse);
				HologramWitherSkull witherSkull = HolographicDisplays.getNmsManager().spawnHologramWitherSkull(bukkitWorld, x, y + VERTICAL_OFFSET - (i*lineSpacing), z, this);
				witherSkulls.add(witherSkull);
				horse.rideSkull(witherSkull); // Let the horse ride the wither skull.
				if (line.length() > 0) {
					horse.forceSetCustomName(line); // Other plugins cannot change it.
				}
				horse.setLockTick(true);
				witherSkull.setLockTick(true);
				
				// Placeholders.
				HolographicDisplays.getPlaceholderManager().trackIfNecessary(horse);
			}
			
		} catch (SpawnFailedException ex) {
			// Kill the entities and return false.
			hide();
			return false;
		}
		
		return true;
	}


	public void hide() {
		for (HologramHorse horse : horses) {
			horse.killEntity();
		}
		for (HologramWitherSkull witherSkull : witherSkulls) {
			witherSkull.killEntity();
		}
	}
	
	@Override
	public String toString() {
		return "CraftHologram{linesAmount=" + lines.size() + ",x=" + x + ",y=" + y + ",z=" + z + ",world=" + bukkitWorld.getName() + "}";
	}
}
