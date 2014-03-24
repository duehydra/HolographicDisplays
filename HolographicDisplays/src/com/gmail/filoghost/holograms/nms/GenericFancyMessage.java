package com.gmail.filoghost.holograms.nms;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface GenericFancyMessage {

	public GenericFancyMessage color(final ChatColor color);

	public GenericFancyMessage style(final ChatColor... styles);
	
	public GenericFancyMessage file(final String path);
	
	public GenericFancyMessage link(final String url);
	
	public GenericFancyMessage suggest(final String command);
	
	public GenericFancyMessage command(final String command);
	
	public GenericFancyMessage achievementTooltip(final String name);
	
	public GenericFancyMessage itemTooltip(final String itemJSON);
	
	public GenericFancyMessage itemTooltip(final ItemStack itemStack);
	
	public GenericFancyMessage tooltip(final String text);

	public GenericFancyMessage then(final Object obj);
	
	public String toJSONString();
	
	public void send(Player player);
	
}
