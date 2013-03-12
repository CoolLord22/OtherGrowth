package com.gmail.zariust.mcplugins.othergrowth;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;



public class RunSync implements Runnable {

	private final OtherGrowth plugin;
	public RunSync(OtherGrowth plugin) {
		this.plugin = plugin;
	}
	
/*
	private void checkNearAndReplace(Block block, Material require, Material replacement, Double chance) {
		for (BlockFace face : BlockFace.values()) {
			Block relativeBlock = block.getRelative(face);
			if (relativeBlock !=null && relativeBlock.getType() == require) {
				// note: can't modify in async so store in a map that a sync thread will check
				// or trigger an event
				if (rolldice(chance)) {
					triggerEvent(block, replacement); 
				}
			}
		}
	}

	//world level conditions: world, time, weather
	//block level conditions: biome, region, height, lightlevel

	private void triggerEvent(Block block, Material replacement) {
		Bukkit.getServer().broadcastMessage("Working!");		
	}

*/

	private void SyncProcess() {
		Log.high("Starting sync replacements...");
		int count = 0;
		
		MatchResult result = OtherGrowth.results.poll();
		while (result != null) {
			count++;
			ChunkSnapshot chunkSnapshot = result.getChunkSnapshot();
			Location loc = result.getLocation();
			World world = Bukkit.getServer().getWorld(chunkSnapshot.getWorldName());
			
			Block block = world.getChunkAt(chunkSnapshot.getX(), chunkSnapshot.getZ()).getBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

			Recipe recipe = result.getRecipe();
			
			Log.highest("Got a result: "+block.getType().toString());
			// check faces
			boolean neededMatch = false;
			
			// FIXME: don't really want to do this once per recipe - rearrange so 
			// we can do this outside the recipe and save the number/type of blocks in a map or something
			if (recipe.needed != null) {
				Log.highest("Searching for needed: "+recipe.needed);
				for (BlockFace face : BlockFace.values()) {
					if (block.getRelative(face).getType() == recipe.needed) neededMatch = true;
				} 
			} else {
				neededMatch = true; // null material needed
			}

			if (neededMatch) {
				Log.highest("Replacing with: "+recipe.replacementMat);
				block.setType(recipe.replacementMat);
			}
			//	Block block = world.getBlock(x,y,z);
			//		recipe = recipeSet.get(block.getType());
			//		if (recipe != null) {
			//			if (block.getBiome().matches(recipe.biome)) {
			//					checkNearAndReplace(block, recipe.require, recipe.replacement, recipe.chance);
			//				}
			//	}
			//	}
			//	}

			
			result = OtherGrowth.results.poll();
		}
		Log.high("Sync complete, replaced ("+count+" blocks)");
	}

	public void run() {
		SyncProcess();
	}
}
