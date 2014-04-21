package com.gmail.filoghost.holograms.placeholders;

public abstract class Placeholder {
	
	private String longPlaceholder;
	private String shortPlaceholder;
	
	// 1 longer refresh tick = 4 normal ticks = 1/5 of second.
	private int longerRefreshTicks;
	
	// To avoid exceptions, just use the long placeholder as default;
	protected String currentReplacement;
	
	public Placeholder(String longPlaceholder, String shortPlaceholder, int longTicks) {
		this.longPlaceholder = longPlaceholder;
		this.shortPlaceholder = shortPlaceholder;
		this.longerRefreshTicks = longTicks;
		currentReplacement = longPlaceholder;
	}
	
	public int getLongRefreshTicks() {
		return longerRefreshTicks;
	}
	
	public String getLongPlaceholder() {
		return longPlaceholder;
	}
	
	public String getShortPlaceholder() {
		return shortPlaceholder;
	}
	
	public abstract void update();

	public CharSequence getReplacement() {
		return currentReplacement;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof Placeholder) {
			return ((Placeholder) obj).longPlaceholder.equals(this.longPlaceholder);
		}
		
		return false;
	}
}
