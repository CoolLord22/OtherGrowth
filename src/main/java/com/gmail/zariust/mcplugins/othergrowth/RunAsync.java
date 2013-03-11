package com.gmail.zariust.mcplugins.othergrowth;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class RunAsync implements Runnable {

	private final OtherGrowth plugin;
	public RunAsync(OtherGrowth plugin) {
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
		Log.high("Starting async scan...");
		gatherChunks();
		for (World world : Bukkit.getServer().getWorlds()) {
			//			recipeSet = allRecipies.get(world, time, weather);
			if (OtherGrowth.gatheredChunks.get(world) == null) return;
			
			long count = 0;
			for (ChunkSnapshot chunk : OtherGrowth.gatheredChunks.get(world)) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 0; y < world.getMaxHeight(); y++) {
							count++;
							if (OtherGrowthConfig.globalMaterialToReplace != null && OtherGrowthConfig.globalMaterialToReplaceWith != null) {
								if (chunk.getBlockTypeId(x, y, z) == OtherGrowthConfig.globalMaterialToReplace.getId()) {
									Log.highest("Found "+OtherGrowthConfig.globalMaterialToReplace.toString()+"!!! at "+chunk.getX()+", "+chunk.getZ());

									// FIXME: experimental code only for sync thread:
									if (rolldice(OtherGrowthConfig.globalChanceToReplace)) {
										Block block = world.getChunkAt(chunk.getX(), chunk.getZ()).getBlock(x, y, z);
										block.setType(OtherGrowthConfig.globalMaterialToReplaceWith);
										Biome biome = block.getBiome();
										String faces = "";
										for (BlockFace face : BlockFace.values()) {
											faces += block.getRelative(face).getType().toString();
										}

									}

								}
							}
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
					}
				}
			}
			Log.high("Scan complete ("+count+" blocks)");
		}
	}

	private void gatherChunks() {
		OtherGrowth.gatheredChunks.clear();

		Set<ChunkSnapshot> chunks = new HashSet<ChunkSnapshot>();
		Set<Chunk> chunksSeen = new HashSet<Chunk>();
		Map<Integer, Boolean> chunksSeenX = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> chunksSeenZ = new HashMap<Integer, Boolean>();
		
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			
			int chunkRadius = OtherGrowth.config.globalChunkScanRadius;
			
			ChunkSnapshot playerChunk = player.getLocation().getChunk().getChunkSnapshot();
			Log.high("Scanning chunks "+chunkRadius+" radius around "+player.getDisplayName()+" ("+playerChunk.getX()+", "+playerChunk.getZ()+")");
			for (int x = (-1*chunkRadius); x<=(chunkRadius); x++) {
				for (int z = (-1*chunkRadius); z<=(chunkRadius); z++) {
					if (!chunksSeen.contains(player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z))) {
						chunksSeen.add(player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z));

						ChunkSnapshot currentChunkSnapShot = player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z).getChunkSnapshot();
						Log.highest("Saving chunk "+currentChunkSnapShot.getX()+", "+currentChunkSnapShot.getZ());

						chunks.add(currentChunkSnapShot);
						OtherGrowth.gatheredChunks.put(player.getWorld(), chunks);
					}
				}
			}
		}
	}
// note: caution - concurrent modification exceptions?
// better to trigger an event for each block that matched?  max 

//about 3000 blocks per recipe, prob closer to 30 actual matches 

//(assume 1% of chunk matches recipe.require & 0.05% chance value

/*SyncProcess(replaceBlocks()) {
  myMap = replaceMap.clone()
  for (Entry entry : myMap.getEntries()) {
    entry.key.setType(entry.value);
    replaceMap.remove(entry.key);
  }

}*/

	public void run() {
		aSyncProcessScanBlocks();
	}
}
