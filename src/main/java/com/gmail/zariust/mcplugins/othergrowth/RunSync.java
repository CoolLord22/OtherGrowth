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
			block.setType(OtherGrowthConfig.globalMaterialToReplaceWith);

			// check faces
			String faces = "";
			for (BlockFace face : BlockFace.values()) {
				faces += block.getRelative(face).getType().toString();
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
