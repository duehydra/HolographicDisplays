package com.gmail.filoghost.holograms.nms.interfaces;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface HologramHorse {
	
	public void setLockTick(boolean lock);
	
	public void forceSetCustomName(String name);
	
	public CraftHologram getHologram();
	
	public void rideSkull(HologramWitherSkull skull);
	
	public void die();
	
	public boolean isDead();
	
	public String getCustomName();
	
}
