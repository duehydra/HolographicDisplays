package com.gmail.filoghost.holograms.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.gmail.filoghost.holograms.exception.InvalidCharactersException;

public class StringUtils {
	private static Map<String, String> placeholders;
	static {
		placeholders = new HashMap<String, String>();
		placeholders.put("[x]", "\u2588"); // Full block
		placeholders.put("[/]", "\u258C"); // Half block
		placeholders.put("[.]", "\u2591"); // 25% opacity block
		placeholders.put("[..]", "\u2592"); // 50% opacity block
		placeholders.put("[...]", "\u2593"); // 75% opacity block
		placeholders.put("[p]", "\u2022"); // Black point
		placeholders.put("[|]", "\u23B9"); // Vertical bar
		
	}
	
	public static String toReadableFormat(String input) {
		if (input == null || input.length() == 0) return input;

		for (Entry<String, String> entry : placeholders.entrySet()) {
			input = input.replace(entry.getKey(), entry.getValue());
		}
		
		input = ChatColor.translateAlternateColorCodes('&', input);
		return input;
	}
	
	public static String toSaveableFormat(String input) {
		if (input == null || input.length() == 0) return input;
		
		for (Entry<String, String> entry : placeholders.entrySet()) {
			input = input.replace(entry.getValue(), entry.getKey());
		}
		
		input = input.replace("§", "&");
		return input;
	}
	
	public static <T> String join(T[] objects, String separator, int start, int end) {
		return org.apache.commons.lang.StringUtils.join(objects, separator, start, end);
	}
	
	
	private static final char[] VALID_HOLOGRAM_NAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_".toCharArray();
	public static String validateName(String name) throws InvalidCharactersException {
		for (char c : name.toCharArray()) {
			if (!isValidNameChar(c)) {
				throw new InvalidCharactersException(Character.toString(c));
			}
		}
		return name;
	}
	
	private static boolean isValidNameChar(char c) {
		for (char validChar : VALID_HOLOGRAM_NAME_CHARS) {
			if (c == validChar) {
				return true;
			}
		}
		return false;
	}
	
}
