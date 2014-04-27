package com.gmail.filoghost.holograms.protocol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holograms.HolographicDisplays;
import com.gmail.filoghost.holograms.object.CraftHologram;

public class ProtocolLibHook {
	
	public static void initialize() {
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			
			HolographicDisplays.getInstance().getLogger().info("Found ProtocolLib, adding support for {player} variable.");

			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HolographicDisplays.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA) {
						  
					
					@Override
					public void onPacketSending(PacketEvent event) {
						
						PacketContainer packet = event.getPacket();

						// Spawn entity packet
						if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

							WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet);
							Entity entity = spawnEntityPacket.getEntity(event);
							
							if (entity == null || (entity.getType() != EntityType.HORSE && entity.getType() != EntityType.WITHER_SKULL)) {
								return;
							}
							
							CraftHologram hologram = getHologram(entity);
							if (hologram == null) {
								return;
							}
							
							Player player = event.getPlayer();
							if (hologram.useVisibilityManager() && !hologram.getVisibilityManager().isVisibleTo(player)) {
								event.setCancelled(true);
								return;
							}
							
							WrappedDataWatcher dataWatcher = spawnEntityPacket.getMetadata();
							String customName = dataWatcher.getString(10);
								
							if (customName.contains("{player}") || customName.contains("{displayname}")) {

								WrappedDataWatcher dataWatcherClone = dataWatcher.deepClone();
								dataWatcherClone.setObject(10, customName.replace("{player}", player.getName()).replace("{displayname}", player.getDisplayName()));
								spawnEntityPacket.setMetadata(dataWatcherClone);
								event.setPacket(spawnEntityPacket.getHandle());
									
							}

						// Entity metadata packet
						} else {
							
							WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet);
							Entity entity = entityMetadataPacket.getEntity(event);
							
							if (entity == null || (entity.getType() != EntityType.HORSE && entity.getType() != EntityType.WITHER_SKULL)) {
								return;
							}
							
							CraftHologram hologram = getHologram(entity);
							if (hologram == null) {
								return;
							}
							
							Player player = event.getPlayer();
							if (hologram.useVisibilityManager() && !hologram.getVisibilityManager().isVisibleTo(player)) {
								event.setCancelled(true);
								return;
							}

							List<WrappedWatchableObject> dataWatcherValues = entityMetadataPacket.getEntityMetadata();
								
							for (int i = 0; i < dataWatcherValues.size(); i++) {	
								
								if (dataWatcherValues.get(i).getIndex() == 10) {
										
									Object customNameObject = dataWatcherValues.get(i).deepClone().getValue();
									if (customNameObject instanceof String == false) {
										return;
									}
									
									String customName = (String) customNameObject;
										
									if (customName.contains("{player}") || customName.contains("{displayname}")) {
										
										entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet.deepClone());
										List<WrappedWatchableObject> clonedList = entityMetadataPacket.getEntityMetadata();
										WrappedWatchableObject clonedElement = clonedList.get(i);
										clonedElement.setValue(customName.replace("{player}", player.getName()).replace("{displayname}", player.getDisplayName()));
										entityMetadataPacket.setEntityMetadata(clonedList);
										event.setPacket(entityMetadataPacket.getHandle());
										return;
											
									}
								}
							}
						}
					}	
				});
		}
	}
	
	private static CraftHologram getHologram(Entity bukkitEntity) {
		return HolographicDisplays.getNmsManager().getHologram(bukkitEntity);
	}
}
