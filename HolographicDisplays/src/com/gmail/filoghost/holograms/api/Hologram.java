package com.gmail.filoghost.holograms.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.filoghost.holograms.object.APIHologramManager;

public abstract class Hologram {

	protected boolean deleted;
	
	/**
	 * @return false if the spawn was blocked.
	 */
	public abstract boolean update();
	
	/**
	 * Hide the hologram.
	 */
	public abstract void hide();
	
	/**
	 * Append a line at the end.
	 */
	public abstract void addLine(String text);

	/**
	 * Remove a line at the given index (0 = first line)
	 */
	public abstract void removeLine(int index);

	/**
	 * Change a line at the given index (0 = first line).
	 */
	public abstract void setLine(int index, String text);
	
	/**
	 * Add a line before the given index (0 = first line).
	 */
	public abstract void insertLine(int index, String text);

	/**
	 * @return a copy of the lines.
	 */
	public abstract List<String> getLines();
	
	/**
	 * @return the amount of lines of this hologram.
	 */
	public abstract int getLinesLength();
	
	/**
	 * Remove all the lines from the hologram.
	 */
	public abstract void clearLines();
	
	/**
	 * @return true if the hologram is in the given chunk. This is used by HolographicDisplays to handle chunks loading.
	 */
	public abstract boolean isInChunk(Chunk chunk);
	
	/**
	 * @return the X coordinate of this hologram. 
	 */
	public abstract double getX();
	
	/**
	 * @return the Y coordinate of this hologram. 
	 */
	public abstract double getY();
	
	/**
	 * @return the Z coordinate of this hologram. 
	 */
	public abstract double getZ();
	
	/**
	 * @return the world of this hologram. 
	 */
	public abstract World getWorld();
	
	/**
	 * Change the location of this hologram. You have to call update() after this method.
	 */
	public abstract void setLocation(Location location);
	
	/**
	 * Deletes this hologram, removing it from the lists.
	 */
	public void delete() {
		deleted = true;
		APIHologramManager.remove(this);
	}
	
}
