package com.gmail.filoghost.holograms.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.exception.SpawnFailedException;

/**
 * This class is only used by the plugin itself. Other plugins should just use the API.
 */
public class CraftHologram extends Hologram {

	private List<HologramLine> lines;
	
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
		lines = new ArrayList<HologramLine>();
	}
	
	public void setLocation(Location source) {
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
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int getZ() {
		return (int) z;
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
	
	private boolean isInLoadedChunk() {
		return bukkitWorld.isChunkLoaded(chunkX, chunkZ);
	}
	
	public String getWorldName() {
		return bukkitWorld.getName();
	}
	
	public void addLine(String message) {
		if (message == null) {
			message = "";
		}
		
		if (message.length() > 300) {
			message = message.substring(0, 300);
		}
		
		lines.add(new HologramLine(this, message));
	}
	
	public void insertLine(int index, String message) {
		if (message == null) {
			message = "";
		}
		
		if (message.length() > 300) {
			message = message.substring(0, 300);
		}
		
		lines.add(index, new HologramLine(this, message));
	}

	public List<String> getLines() {
		List<String> stringLines = new ArrayList<String>(this.lines.size());
		for (HologramLine hologramLine : lines) {
			if (hologramLine.getText() != null) {
				stringLines.add(hologramLine.getText());
			} else {
				stringLines.add("");
			}
		}
		return stringLines;
	}
	
	public void setLine(int index, String text) {
		if (text == null) {
			text = "";
		}
		
		if (text.length() > 300) {
			text = text.substring(0, 300);
		}
		
		lines.set(index, new HologramLine(this, text));
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
		
		// Remove previous entities.
		hide();
		
		try {
			
			for (int i = 0; i < lines.size(); i++) {
				lines.get(i).spawn(bukkitWorld, x, y - (HolographicDisplays.getVerticalLineSpacing()*i), z);
			}
			
		} catch (SpawnFailedException ex) {
			// Kill the entities and return false.
			hide();
			return false;
		}
		
		return true;
	}


	public void hide() {
		for (HologramLine line : lines) {
			line.remove();
		}
	}
	
	@Override
	public String toString() {
		return "CraftHologram{linesAmount=" + lines.size() + ",x=" + x + ",y=" + y + ",z=" + z + ",world=" + bukkitWorld.getName() + "}";
	}
}
