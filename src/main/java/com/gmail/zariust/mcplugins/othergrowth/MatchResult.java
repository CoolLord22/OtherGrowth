package com.gmail.zariust.mcplugins.othergrowth;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

public class MatchResult {
	private ChunkSnapshot chunkSnapshot = null;
	private Location location = null;
	
	public MatchResult(ChunkSnapshot chunk, Object recipe, Location location) {
		this.setChunkSnapshot(chunk);
		this.location = location;
	}

	public ChunkSnapshot getChunkSnapshot() {
		return chunkSnapshot;
	}

	public void setChunkSnapshot(ChunkSnapshot chunkSnapshot) {
		this.chunkSnapshot = chunkSnapshot;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
