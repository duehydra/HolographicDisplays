package com.gmail.filoghost.holograms.nms;

import com.gmail.filoghost.holograms.object.CraftHologram;

public interface GenericEntityHologramWitherSkull {
	
	public void setLockTick(boolean lock);
	
	public CraftHologram getHologram();
	
	public void die();
}
