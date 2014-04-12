package com.gmail.filoghost.holograms.utils;


public enum ConfigNode {

	VERTICAL_SPACING("vertical-spacing", 0.25),
	IMAGES_SYMBOL("images.symbol", "[x]"),
	TRANSPARENCY_SPACE("images.transparency.space", " [|] "),
	TRANSPARENCY_COLOR("images.transparency.color", "&8");
	
	private String path;
	private Object value;
	
	private ConfigNode(String path, Object defaultValue) {
		this.path = path;
		value = defaultValue;
	}
	
	public String getPath() {
		return path;
	}
	
	public Object getDefault() {
		return value;
	}
}
