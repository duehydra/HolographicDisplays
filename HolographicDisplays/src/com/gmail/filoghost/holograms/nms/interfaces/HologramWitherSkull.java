package com.gmail.filoghost.holograms.nms.interfaces;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface HologramWitherSkull {
	
	public void setLockTick(boolean lock);
	
	public CraftHologram getHologram();
	
	public void die();

}