package com.gmail.filoghost.holograms.utils;

import org.bukkit.Bukkit;

public class VersionUtils {

	public static String getBukkitVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}
	
}
