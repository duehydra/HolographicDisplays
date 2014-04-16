package com.gmail.filoghost.holograms.nms.interfaces;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface HologramHorse {
	
	public void setLockTick(boolean lock);
	
	public void forceSetCustomName(String name);
	
	public CraftHologram getHologram();
	
	public void rideSkull(HologramWitherSkull skull);
	
	public void killEntity();
	
	public boolean isDead();
	
	public String getEntityCustomName();
	
	public void setNMSLocation(double x, double y, double z);
	
}
