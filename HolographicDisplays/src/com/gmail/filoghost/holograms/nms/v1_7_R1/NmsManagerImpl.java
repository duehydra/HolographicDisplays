package com.gmail.filoghost.holograms.nms.v1_7_R1;

import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.gmail.filoghost.holograms.exception.SpawnFailedException;
import com.gmail.filoghost.holograms.nms.interfaces.FancyMessage;
import com.gmail.filoghost.holograms.nms.interfaces.HologramHorse;
import com.gmail.filoghost.holograms.nms.interfaces.HologramWitherSkull;
import com.gmail.filoghost.holograms.nms.interfaces.NmsManager;
import com.gmail.filoghost.holograms.object.CraftHologram;
import com.gmail.filoghost.holograms.object.HologramComponent;
import com.gmail.filoghost.holograms.utils.ReflectionUtils;

import net.minecraft.server.v1_7_R1.EntityTypes;
import net.minecraft.server.v1_7_R1.EntityWitherSkull;
import net.minecraft.server.v1_7_R1.WorldServer;
import net.minecraft.server.v1_7_R1.Entity;

public class NmsManagerImpl implements NmsManager {

	@Override
	public void registerCustomEntities() throws Exception {
		registerCustomEntity(EntityHologramHorse.class, "EntityHorse", 100);
		registerCustomEntity(EntityHologramWitherSkull.class, "WitherSkull", 19);
	}
	
	@SuppressWarnings("rawtypes")
	public void registerCustomEntity(Class entityClass, String name, int id) throws Exception {
		ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "d", entityClass, name);
		ReflectionUtils.putInPrivateStaticMap(EntityTypes.class, "f", entityClass, Integer.valueOf(id));
	}
	
	@Override
	public HologramHorse spawnHologramHorse(org.bukkit.World world, double x, double y, double z, CraftHologram parent) throws SpawnFailedException {
		WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		EntityHologramHorse invisibleHorse = new EntityHologramHorse(nmsWorld, parent);
		invisibleHorse.setLocation(x, y, z, 0.0F, 0.0F);
		if (!nmsWorld.addEntity(invisibleHorse, SpawnReason.CUSTOM)) {
			throw new SpawnFailedException();
		}
		return invisibleHorse;
	}
	
	@Override
	public HologramWitherSkull spawnHologramWitherSkull(org.bukkit.World bukkitWorld, double x, double y, double z, CraftHologram parent) throws SpawnFailedException {
		WorldServer nmsWorld = ((CraftWorld) bukkitWorld).getHandle();
		EntityHologramWitherSkull staticWitherSkull = new EntityHologramWitherSkull(nmsWorld, parent);
		staticWitherSkull.setLocation(x, y, z, 0.0F, 0.0F);
		if (!nmsWorld.addEntity(staticWitherSkull, SpawnReason.CUSTOM)) {
			throw new SpawnFailedException();
		}
		return staticWitherSkull;
	}
	
	public void removeWitherSkulls(Chunk chunk) {
		net.minecraft.server.v1_7_R1.Entity nmsEntity;
		
		// Remove all the WitherSkulls.
		for (org.bukkit.entity.Entity entity : chunk.getEntities()) {		
			if (!entity.isDead()) {
				nmsEntity = ((CraftEntity) entity).getHandle();
				if (nmsEntity instanceof EntityWitherSkull) {
					EntityWitherSkull skull = (EntityWitherSkull) nmsEntity;
					if (skull.passenger != null) {
						skull.passenger.die();
					}
					skull.die();
				}
			}
		}
	}
	
	@Override
	public boolean isHologramEntity(org.bukkit.entity.Entity bukkitEntity) {
		return ((CraftEntity) bukkitEntity).getHandle() instanceof HologramComponent;
	}

	@Override
	public CraftHologram getHologram(org.bukkit.entity.Entity bukkitEntity) {
		
		Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
		if (nmsEntity instanceof HologramComponent) {
			return ((HologramComponent) nmsEntity).getHologram();
		}

		return null;
	}
	
	@Override
	public FancyMessage newFancyMessage(String text) {
		return new FancyMessageImpl(text);
	}
	
	@Override
	public int getCustomNameLimit() {
		return 300;
	}

	@Override
	public boolean hasChatHoverFeature() {
		return true;
	}
	
}