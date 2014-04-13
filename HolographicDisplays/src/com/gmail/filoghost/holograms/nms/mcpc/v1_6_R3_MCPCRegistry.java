package com.gmail.filoghost.holograms.nms.mcpc;

import com.gmail.filoghost.holograms.utils.ReflectionUtils;

public class v1_6_R3_MCPCRegistry {

	public static void registerCustomEntity(Class<?> clazz, String entityName, int entityID) throws Exception {
		
		Class<?> entityTypesClass = Class.forName("net.minecraft.server.v1_6_R3.EntityTypes");
		
		/* Used to read the fields in the class EntityTypes
		for (Field f : entityTypesClass.getDeclaredFields()) {
			Object o = f.get(null);
			System.out.println(f.getName() + ":"+ o.getClass().getName());
			if (o instanceof Map) {
				Map map = (Map) o;
				System.out.println("First entry classes: " + ((Map.Entry<?, ?>)map.entrySet().iterator().next()).getKey().getClass().getName()+ ", " + ((Map.Entry<?, ?>)map.entrySet().iterator().next()).getValue().getClass().getName());
			}
		}
		*/
		
		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75626_c", clazz, entityName);
		ReflectionUtils.putInPrivateStaticMap(entityTypesClass, "field_75624_e", clazz, Integer.valueOf(entityID));
	}
}
