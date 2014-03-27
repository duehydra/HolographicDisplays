package com.gmail.filoghost.holograms.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.nms.GenericEntityHologramHorse;
import com.gmail.filoghost.holograms.nms.GenericEntityHologramWitherSkull;

/**
 * This class is only used by the plugin itself. Other plugins should just use the API.
 */

//TODO Nota importante: fare in modo che le entità che contengono dei placeholder vengano messe da parte per essere aggiornate. vengono cambiare solo con refresh!
public class CraftHologram extends Hologram {

	private static final double VERTICAL_OFFSET = 54.4;
	
	private List<GenericEntityHologramHorse> horses;
	private List<GenericEntityHologramWitherSkull> witherSkulls;
	
	private List<String> lines;	
	
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
		horses = new ArrayList<GenericEntityHologramHorse>();
		witherSkulls = new ArrayList<GenericEntityHologramWitherSkull>();
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
		
		lines.add(message);
	}
	
	public void insertLine(int index, String message) {
		if (message == null) {
			message = "";
		}
		
		if (message.length() > 300) {
			message = message.substring(0, 300);
		}
		
		lines.add(index, message);
	}

	public List<String> getLines() {
		return new ArrayList<String>(lines);
	}
	
	public void setLine(int index, String text) {
		if (text == null) {
			text = "";
		}
		
		if (text.length() > 300) {
			text = text.substring(0, 300);
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
		
		// Remove previous entities.
		hide();
		
		try {
			
			double lineSpacing = HolographicDisplays.getVerticalLineSpacing();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				
				if (line == null || line.length() == 0) continue;
				GenericEntityHologramHorse horse = HolographicDisplays.getNmsManager().spawnHologramHorse(bukkitWorld, x, y + VERTICAL_OFFSET - (i*lineSpacing), z, this);
				horses.add(horse);
				GenericEntityHologramWitherSkull witherSkull = HolographicDisplays.getNmsManager().spawnHologramWitherSkull(bukkitWorld, x, y + VERTICAL_OFFSET - (i*lineSpacing), z, this);
				witherSkulls.add(witherSkull);
				horse.rideSkull(witherSkull); // Let the horse ride the wither skull.
				horse.forceSetCustomName(line); // Other plugins cannot change it.
				horse.setLockTick(true);
				witherSkull.setLockTick(true);
			}
			
		} catch (SpawnFailedException ex) {
			// Kill the entities and return false.
			hide();
			return false;
		}
		
		return true;
	}


	public void hide() {
		for (GenericEntityHologramHorse horse : horses) {
			horse.die();
		}
		for (GenericEntityHologramWitherSkull witherSkull : witherSkulls) {
			witherSkull.die();
		}
	}
	
	@Override
	public String toString() {
		return "CraftHologram{linesAmount=" + lines.size() + ",x=" + x + ",y=" + y + ",z=" + z + ",world=" + bukkitWorld.getName() + "}";
	}
}
