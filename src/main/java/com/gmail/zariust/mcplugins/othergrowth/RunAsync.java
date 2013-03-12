package com.gmail.zariust.mcplugins.othergrowth;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RunAsync implements Runnable {

	private final OtherGrowth plugin;
	public RunAsync(OtherGrowth plugin) {
		this.plugin = plugin;
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
		Log.high("Starting async scan...");
		gatherChunks();
		for (World world : Bukkit.getServer().getWorlds()) {
			//			recipeSet = allRecipies.get(world, time, weather);
			if (OtherGrowth.gatheredChunks.get(world) == null) return;
			
			long count = 0;
			for (ChunkSnapshot chunk : OtherGrowth.gatheredChunks.get(world)) {
				if (chunk == null) {
					Log.warning("Gathered chunk is null??");
					continue;
				}

				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 0; y < world.getMaxHeight(); y++) {
							count++;
							if (OtherGrowthConfig.globalMaterialToReplace != null && OtherGrowthConfig.globalMaterialToReplaceWith != null) {
								if (chunk.getBlockTypeId(x, y, z) == OtherGrowthConfig.globalMaterialToReplace.getId()) {
									Log.highest("Found "+OtherGrowthConfig.globalMaterialToReplace.toString()+"!!! at "+chunk.getX()+", "+chunk.getZ());
									// FIXME: experimental code only for sync thread:
									if (rolldice(OtherGrowthConfig.globalChanceToReplace)) {
										// check biome
										//Biome biome = chunk.getBiome(x, z); // FIXME: gives regular npe's?

										synchronized (plugin) {
											Object recipe = null;
											OtherGrowth.results.add(new MatchResult(chunk, recipe, new Location(null, x, y, z) ));
										}
									}

								}
							}
						}
					}
				}
			}
			Log.high("Scan complete ("+count+" blocks)");
		}
	}


	final static Map<World, Set<ChunkSnapshot>> gatheredChunks = new HashMap<World, Set<ChunkSnapshot>>();

	private void gatherChunks() {
		OtherGrowth.gatheredChunks.clear();
		
		synchronized (plugin) {
			
			Set<ChunkSnapshot> chunks = new HashSet<ChunkSnapshot>();
			Set<Chunk> chunksSeen = new HashSet<Chunk>();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {

				int chunkRadius = OtherGrowthConfig.globalChunkScanRadius;

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
	}

	public void run() {
		aSyncProcessScanBlocks();
	}
}
