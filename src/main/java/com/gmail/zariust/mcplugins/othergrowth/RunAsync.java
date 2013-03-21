package com.gmail.zariust.mcplugins.othergrowth;


import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;

public class RunAsync implements Runnable {
	private final OtherGrowth plugin;
	public RunAsync(OtherGrowth plugin) {
		this.plugin = plugin;
	}

	public void run() {
		aSyncProcessScanBlocks();
	}

	private boolean rolldice(Double chance) {
		Double rolledValue = OtherGrowth.rng.nextDouble();
		boolean chancePassed = rolledValue <= chance / 100.0; 
		if (!chancePassed) {
			return false;
		} else {
			return true;
		}
	}

	private void aSyncProcessScanBlocks() {
		long startTime = System.currentTimeMillis();
		Log.high("Starting async scan... gatheredChunks size: "+OtherGrowth.gatheredChunks.size());

		long count = 0;
		ChunkSnapshot chunk = OtherGrowth.gatheredChunks.poll();
		boolean reportedRecipes = false;
		while (chunk != null) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = 0; y < Bukkit.getServer().getWorld(chunk.getWorldName()).getMaxHeight(); y++) {
						count++;
						int currentMaterial = chunk.getBlockTypeId(x,  y,  z);

						Set<Recipe> recipes = OtherGrowth.recipes.get(chunk.getWorldName());
						if (recipes != null) {
							for (Recipe recipe : recipes) {
								if (!reportedRecipes) {
									Log.high("Testing recipe: "+recipe.name);
									reportedRecipes = true;
								}
								
								if (recipe.target != null && recipe.replacementMat != null) {
									if (currentMaterial == recipe.target.getId()) {
										Log.highest("Found "+recipe.target.toString()+"!!! at "+chunk.getX()+", "+chunk.getZ());

										if (rolldice(recipe.chance)) {
											// check biome
											//Biome biome = chunk.getBiome(x, z); // FIXME: gives regular npe's?

											OtherGrowth.results.add(new MatchResult(chunk, recipe, new Location(null, x, y, z) ));
										}
									}
								}
							}
						}
					}
				}
			}
			chunk = OtherGrowth.gatheredChunks.poll();
		}
		long finishedTime = System.currentTimeMillis();
		Log.high("Scan complete ("+count+" blocks) took "+((finishedTime - startTime)/1000)+"secs");
	}
}
