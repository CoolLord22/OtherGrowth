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
	final Map<String, Set<ChunkSnapshot>> gatheredChunks = new HashMap<String, Set<ChunkSnapshot>>();

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
		Log.high("Starting async scan...");
		if (OtherGrowthConfig.globalScanLoadedChunks) {
			gatherLoadedChunks();
		} else {
			gatherChunks();
		}
		for (String world : gatheredChunks.keySet()) {
			//			recipeSet = allRecipies.get(world, time, weather);
			if (gatheredChunks.get(world) == null) {
				
				return;
			}
			
			long count = 0;
			for (ChunkSnapshot chunk : gatheredChunks.get(world)) {
				if (chunk == null) {
					Log.warning("Gathered chunk is null??");
					continue;
				}

				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 0; y < Bukkit.getServer().getWorld(world).getMaxHeight(); y++) {
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

	private void gatherLoadedChunks() {
		gatheredChunks.clear();

		synchronized (plugin) {
			for (World world : Bukkit.getServer().getWorlds()) {
				if (world.getLoadedChunks().length > 0) {
					Log.high("World: "+world.toString());
					Set<ChunkSnapshot> chunkSnaps = new HashSet<ChunkSnapshot>();
					for (Chunk chunk : world.getLoadedChunks()) {
						chunkSnaps.add(chunk.getChunkSnapshot());
					}
					Log.high("Chunks: "+chunkSnaps.size()+" loaded: "+world.getLoadedChunks().length);
					gatheredChunks.put(world.getName(), chunkSnaps);
				}
			}
		}
	}

	private void gatherChunks() {
		gatheredChunks.clear();
		
		synchronized (plugin) {
			
			Set<ChunkSnapshot> chunks = new HashSet<ChunkSnapshot>();
			Set<Chunk> chunksSeen = new HashSet<Chunk>();
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {

				int chunkRadius = OtherGrowthConfig.globalChunkScanRadius;

				ChunkSnapshot playerChunk = player.getLocation().getChunk().getChunkSnapshot();
				Log.high("Scanning chunks "+chunkRadius+" radius around "+player.getDisplayName()+" ("+playerChunk.getX()+", "+playerChunk.getZ()+")");
				Log.high("Total loaded chunks = "+player.getWorld().getLoadedChunks().length);
				
				for (int x = (-1*chunkRadius); x<=(chunkRadius); x++) {
					for (int z = (-1*chunkRadius); z<=(chunkRadius); z++) {
						if (!chunksSeen.contains(player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z))) {

							chunksSeen.add(player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z));

							Chunk chunk = player.getWorld().getChunkAt(playerChunk.getX()+x, playerChunk.getZ()+z);
							if (chunk.isLoaded()) {
								ChunkSnapshot currentChunkSnapShot = chunk.getChunkSnapshot();
								Log.highest("Saving chunk "+currentChunkSnapShot.getX()+", "+currentChunkSnapShot.getZ());

								chunks.add(currentChunkSnapShot);
								gatheredChunks.put(player.getWorld().getName(), chunks);
							}
						}
					}
				}
			}
		}
	}
}
