package com.gmail.filoghost.holograms.protocol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holograms.HolographicDisplays;

public class ProtocolLibHook {
	
	//TODO check if the entity is really a hologram
	
	public static void initialize() {
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			
			HolographicDisplays.getInstance().getLogger().info("Found ProtocolLib, adding support for {player} variable.");

			ProtocolLibrary.getProtocolManager().addPacketListener(
					
				new PacketAdapter(HolographicDisplays.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA) {
						  
					@Override
					public void onPacketSending(PacketEvent event) {
						
						PacketContainer packet = event.getPacket();

						if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

							WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet);
							
							if (!isHorse(spawnEntityPacket.getEntity(event))) {
								return;
							}
								
							
							WrappedDataWatcher dataWatcher = spawnEntityPacket.getMetadata();
							String customName = dataWatcher.getString(10);
								
							if (customName.contains("{player}")) {

									
								WrappedDataWatcher dataWatcherClone = dataWatcher.deepClone();
								dataWatcherClone.setObject(10, customName.replace("{player}", event.getPlayer().getName()));
								spawnEntityPacket.setMetadata(dataWatcherClone);
								event.setPacket(spawnEntityPacket.getHandle());
									
							}

						} else {
							
							WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet);
							
							if (!isHorse(entityMetadataPacket.getEntity(event))) {
								return;
							}

							List<WrappedWatchableObject> dataWatcherValues = entityMetadataPacket.getEntityMetadata();
								
							for (int i = 0; i < dataWatcherValues.size(); i++) {	
								
								if (dataWatcherValues.get(i).getIndex() == 10) {
										
									String customName = (String) dataWatcherValues.get(i).deepClone().getValue();
										
									if (customName.contains("{player}")) {
										
										entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet.deepClone());
										List<WrappedWatchableObject> clonedList = entityMetadataPacket.getEntityMetadata();
										WrappedWatchableObject clonedElement = clonedList.get(i);
										clonedElement.setValue(customName.replace("{player}", event.getPlayer().getName()));
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
	
	private static boolean isHorse(Entity bukkitEntity) {
		return (bukkitEntity !=  null && bukkitEntity.getType() == EntityType.HORSE);
	}
}
