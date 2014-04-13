package com.gmail.filoghost.holograms.nms.mcpc;

import com.gmail.filoghost.holograms.utils.ReflectionUtils;

public class v1_7_R3_MCPCRegistry {

	// This version still doesn't exist, but I suppose that the field names won't change.
	
	public static void registerCustomEntity(Class<?> clazz, String entityName, int entityID) throws Exception {
		
		Class<?> entityTypesClass = Class.forName("net.minecraft.server.v1_7_R3.EntityTypes");

		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75626_c", clazz, entityName);
		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75624_e", clazz, Integer.valueOf(entityID));
	}
}
