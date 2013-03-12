package com.gmail.zariust.mcplugins.othergrowth;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

public class MatchResult {
	private ChunkSnapshot chunkSnapshot = null;
	private Location location = null;
	private Recipe recipe = null;
	
	public MatchResult(ChunkSnapshot chunk, Recipe recipe, Location location) {
		this.setChunkSnapshot(chunk);
		this.location = location;
		this.setRecipe(recipe);
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

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
}
