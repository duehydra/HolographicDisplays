package com.gmail.filoghost.holograms.nms.interfaces;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface HologramWitherSkull {
	
	public void setLockTick(boolean lock);
	
	public CraftHologram getHologram();
	
	public void killEntity();
	
	public void setNMSLocation(double x, double y, double z);

}
