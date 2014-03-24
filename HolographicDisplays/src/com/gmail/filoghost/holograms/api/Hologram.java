package com.gmail.filoghost.holograms.api;

import java.util.List;

import org.bukkit.Chunk;

import com.gmail.filoghost.holograms.object.APIHologramManager;

public abstract class Hologram {

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
	 * Deletes this hologram, removing it from the lists.
	 */
	public void delete() {
		APIHologramManager.remove(this);
	}
	
	/**
	 * @return true if the hologram is in the given chunk. This is used by Holographic Displays to handle chunks loading.
	 */
	public abstract boolean isInChunk(Chunk chunk);
	
}
