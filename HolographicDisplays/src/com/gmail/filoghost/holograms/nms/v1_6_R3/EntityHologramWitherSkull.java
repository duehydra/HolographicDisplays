package com.gmail.filoghost.holograms.nms.v1_6_R3;

import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;

import com.gmail.filoghost.holograms.nms.GenericEntityHologramWitherSkull;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramComponent;

import net.minecraft.server.v1_6_R3.EntityWitherSkull;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.World;

public class EntityHologramWitherSkull extends EntityWitherSkull implements HologramComponent, GenericEntityHologramWitherSkull {

	private boolean lockTick;
	private CraftHologram parent;
	
	public EntityHologramWitherSkull(World world) {
		super(world);
		die();
	}
	
	public EntityHologramWitherSkull(World world, CraftHologram parent) {
		super(world);
		this.parent = parent;
		super.motX = 0.0;
		super.motY = 0.0;
		super.motZ = 0.0;
		super.dirX = 0.0;
		super.dirY = 0.0;
		super.dirZ = 0.0;
		super.boundingBox.a = 0.0;
		super.boundingBox.b = 0.0;
		super.boundingBox.c = 0.0;
		super.boundingBox.d = 0.0;
		super.boundingBox.e = 0.0;
		super.boundingBox.f = 0.0;
		a(0.0F, 0.0F);
	}
	
	
	@Override
	public void b(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
	}
	
	@Override
	public boolean c(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
		return false;
	}

	@Override
	public boolean d(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
		return false;
	}
	
	@Override
	public void e(NBTTagCompound nbttagcompound) {
		// Do not save NBT.
	}
	
	
	@Override
	public boolean isInvulnerable() {
		/* 
		 * The field Entity.invulnerable is private.
		 * It's only used while saving NBTTags, but since the entity would be killed
		 * on chunk unload, we prefer to override isInvulnerable().
		 */
	    return true;
	}

	@Override
	public void l_() {
		if (!lockTick) {
			super.l_();
		}
	}
	
	@Override
	public void makeSound(String sound, float f1, float f2) {
	    // Remove sounds.
	}
	
	public void callSuperTick() {
		super.l_();
	}
	
	public CraftHologram getHologram() {
		return parent;
	}
	
	public void setLockTick(boolean lock) {
		lockTick = lock;
	}
	
	public void die() {
		setLockTick(false);
		super.die();
	}
	
	public CraftEntity getBukkitEntity() {
		if (super.bukkitEntity == null) {
			this.bukkitEntity = new CraftHologramWitherSkull(this.world.getServer(), this);
	    }
		return this.bukkitEntity;
	}

}