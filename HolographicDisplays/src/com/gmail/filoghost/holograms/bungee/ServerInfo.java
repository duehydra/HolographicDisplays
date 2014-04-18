package com.gmail.filoghost.holograms.bungee;

public class ServerInfo {

	private int onlinePlayers;
	private long lastPing;
	private long lastRequest;

	public ServerInfo(int onlinePlayers, long lastPing) {
		this.onlinePlayers = onlinePlayers;
		this.lastPing = lastPing;
	}

	public int getOnlinePlayers() {
		return onlinePlayers;
	}

	public void setOnlinePlayers(int onlinePlayers) {
		this.onlinePlayers = onlinePlayers;
	}

	public long getLastPing() {
		return lastPing;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}

	public long getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(long lastRequest) {
		this.lastRequest = lastRequest;
	}
}
