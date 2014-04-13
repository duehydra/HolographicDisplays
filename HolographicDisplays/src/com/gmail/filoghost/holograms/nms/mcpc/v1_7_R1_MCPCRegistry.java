package com.gmail.filoghost.holograms.nms.mcpc;

import com.gmail.filoghost.holograms.utils.ReflectionUtils;

public class v1_7_R1_MCPCRegistry {

	public static void registerCustomEntity(Class<?> clazz, String entityName, int entityID) throws Exception {
		
		Class<?> entityTypesClass = Class.forName("net.minecraft.server.v1_7_R1.EntityTypes");

		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75626_c", clazz, entityName);
		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75624_e", clazz, Integer.valueOf(entityID));
	}
}
