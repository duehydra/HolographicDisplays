package com.gmail.filoghost.holograms.protocol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.gmail.filoghost.holograms.HolographicDisplays;

public class ProtocolLibHook {

	private static ProtocolManager protocolManager;

	//TODO check if the entity is really a hologram
	
	public static void initialize() {
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
			
			HolographicDisplays.getInstance().getLogger().info("Found ProtocolLib, adding support for {player} variable.");
			
			protocolManager = ProtocolLibrary.getProtocolManager();
			protocolManager.addPacketListener(
				new PacketAdapter(HolographicDisplays.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.ENTITY_METADATA) {
						  
					@Override
					public void onPacketSending(PacketEvent event) {
						
						PacketContainer packet = event.getPacket();

						if (packet.getType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

							WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet);
							
							if (isHorse(spawnEntityPacket.getEntity(event))) {

								String customName = spawnEntityPacket.getMetadata().getString(10);
								
								if (customName.contains("{player}")) {
									
									spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(packet.deepClone());
									
									spawnEntityPacket.getMetadata().setObject(10, customName.replace("{player}", event.getPlayer().getName()));
									event.setPacket(spawnEntityPacket.getHandle());
								}
							}

						} else {
							
							WrapperPlayServerEntityMetadata entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet);
							
							if (isHorse(entityMetadataPacket.getEntity(event))) {

								List<WrappedWatchableObject> dataWatcherValues = entityMetadataPacket.getEntityMetadata();
								for (WrappedWatchableObject value : dataWatcherValues) {
									
									if (value.getIndex() == 10) {
										String customName = (String) value.getValue();
										
										if (customName.contains("{player}")) {
											
											
											entityMetadataPacket = new WrapperPlayServerEntityMetadata(packet.deepClone());
											
											for (WrappedWatchableObject secondValue : entityMetadataPacket.getEntityMetadata()) {
												if (secondValue.getIndex() == 10) {
													secondValue.setValue(customName.replace("{player}", event.getPlayer().getName()));
												}
											}

											event.setPacket(entityMetadataPacket.getHandle());
											return;
											
										}
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
