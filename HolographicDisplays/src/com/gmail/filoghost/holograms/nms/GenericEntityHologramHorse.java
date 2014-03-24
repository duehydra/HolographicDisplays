package com.gmail.filoghost.holograms.nms;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface GenericEntityHologramHorse {
	
	public void setLockTick(boolean lock);
	
	public void forceSetCustomName(String name);
	
	public CraftHologram getHologram();
	
	public void rideSkull(GenericEntityHologramWitherSkull skull); 
	
	public void die();
}
