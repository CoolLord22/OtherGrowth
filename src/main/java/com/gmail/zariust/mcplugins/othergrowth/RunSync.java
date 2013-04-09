package com.gmail.zariust.mcplugins.othergrowth;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.gmail.zariust.mcplugins.othergrowth.common.Log;



public class RunSync implements Runnable {

	private final OtherGrowth plugin;
	//private HashSet<String> regions;
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
		long beforeLoading = System.currentTimeMillis();
		Log.high("Starting sync replacements...");
		if (OtherGrowthConfig.globalScanLoadedChunks) {
			gatherLoadedChunks();
		} else {
			gatherChunks();
		}
		
		long afterLoading = System.currentTimeMillis();
		Log.high("After loading chunks... (elapsed: "+(afterLoading-beforeLoading)+")");

		MatchResult result = OtherGrowth.results.poll();
		int count = 0;
		while (result != null) {
			ChunkSnapshot chunkSnapshot = result.getChunkSnapshot();
			Location loc = result.getLocation().clone();
			World world = Bukkit.getServer().getWorld(chunkSnapshot.getWorldName());			
			Block block = world.getChunkAt(chunkSnapshot.getX(), chunkSnapshot.getZ()).getBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			Recipe recipe = result.getRecipe();
			
			Log.highest("Got a result: "+block.getType().toString());
			boolean neededMatch = false;
			
			neededMatch = checkNeeded(block, recipe, neededMatch);

			// match regions
			//if (!isInRegion(world, block.getLocation().clone(), recipe.regions)) {
			//	neededMatch = false;
			//}
			
			
			if (neededMatch) {
				count++;
				Log.highest("Replacing with: "+recipe.replacementMat.toString());
				block.setTypeIdAndData(recipe.replacementMat.id.getId(), (byte)recipe.replacementMat.data.getData(), true);
			} else {
				Log.highest("Match failed.");
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
		long finishedTime = System.currentTimeMillis();
		Log.high("Sync complete, replaced ("+count+" blocks) (elapsed: "+(finishedTime - beforeLoading)+")");
	}

//	private void setRegions() {
//		regions = new HashSet<String>();
//		if(!Dependencies.hasWorldGuard()) return;
//		Map<String, ProtectedRegion> regionMap = Dependencies.getWorldGuard().getGlobalRegionManager().get(world).getRegions();
//		Vector vec = new Vector(location.getX(), location.getY(), location.getZ());
//		for(String region : regionMap.keySet()) {
//			if(regionMap.get(region).contains(vec))
//				regions.add(region.toLowerCase()); // note: region needs to be lowercase for case insensitive matches
//		}
//	}
	
	/** Check if the current regions match the configured regions.
	 * 
	 * @param inRegions - a set of regions the player is currently in
	 * @return true if the condition matches
	 */
//	public boolean isInRegion(Set<String> inRegions) {
//		// if no regions configured then all is ok
//		if(regions == null) return true;
//
//		Log.logInfo("Regioncheck: inRegions: " + inRegions.toString(), Verbosity.HIGH);
//		Log.logInfo("Regioncheck: dropRegions: " + regions.toString(), Verbosity.HIGH);
//
//		// save the config region keys in a temp list for some reason (can't remember)
//		HashSet<String> tempConfigRegionKeys = new HashSet<String>();
//		tempConfigRegionKeys.addAll(regions.keySet());
//
//		// set matched flag to false, since we know there's at least something in the customRegion condition
//		boolean matchedRegion = false;
//		int positiveRegions = 0;
//		
//		// loop through each region within the customRegions and check if it matches all current regions
//		for(String dropRegion : tempConfigRegionKeys) {
//			dropRegion = dropRegion.toLowerCase(); // WorldGuard, at least, stores regions in lower case
//			// Check if the entry is an exception (ie. starts with "-")
//			Boolean exception = false;
//			if (dropRegion.startsWith("-")) {
//				Log.logInfo("Checking dropRegion exception: " + dropRegion, Verbosity.EXTREME);
//				exception = true;
//				dropRegion = dropRegion.substring(1);
//			} else {
//				positiveRegions++;
//				Log.logInfo("Checking dropRegion: " + dropRegion, Verbosity.EXTREME);
//			}
//
//			if (exception) {
//				if (inRegions.contains(dropRegion)) {
//					Log.logInfo("Failed check: regions (exception: "+dropRegion+")", Verbosity.HIGH);
//					return false; // if this is an exception and you are in that region then all other checks are moot - hence immediate "return false"
//				} else {
//					Log.logInfo("Exception check: region "+dropRegion+" passed", Verbosity.HIGHEST);					
//				}
//			} else {
//				if (inRegions.contains(dropRegion)) {
//					Log.logInfo("In dropRegion: "+dropRegion+", setting match=TRUE", Verbosity.HIGHEST);
//					matchedRegion = true;
//				} else {
//					//OtherDrops.logInfo("Not in dropRegion: "+dropRegion+", setting match=FALSE", Verbosity.HIGHEST);
//					//matchedRegion = false;
//				}
//
//			}
//
//		}
//		
//		// If there were only exception conditions then return true as we haven't been kicked by a matched exception
//		if (positiveRegions < 1) matchedRegion = true;
//		
//		Log.logInfo("Regioncheck: finished. match="+matchedRegion, Verbosity.HIGH);
//		return matchedRegion;
//	}

	/**
	 * @param block
	 * @param recipe
	 * @param neededMatch
	 * @return
	 */
	private boolean checkNeeded(Block block, Recipe recipe, boolean neededMatch) {
		// FIXME: don't really want to do this once per recipe - rearrange so 
		// we can do this outside the recipe and save the number/type of blocks in a map or something
		if (recipe.needed != null) {
			Log.highest("Searching for needed: "+recipe.needed);
			
			// TODO: make my own enum with only adjacent faces in OtherGrowth class to save processing time here?
			for (BlockFace face : BlockFace.values()) {
				if (face == BlockFace.SELF || 
						face == BlockFace.EAST_NORTH_EAST || face == BlockFace.EAST_SOUTH_EAST || 
						face == BlockFace.NORTH_NORTH_EAST || face == BlockFace.NORTH_NORTH_WEST ||
						face == BlockFace.SOUTH_SOUTH_EAST || face == BlockFace.SOUTH_SOUTH_WEST ||
						face == BlockFace.WEST_NORTH_WEST || face == BlockFace.WEST_SOUTH_WEST) {
					continue;
				}
				if (block.getRelative(face).getType() == recipe.needed.id) neededMatch = true;
				if (recipe.needed.data != null && block.getRelative(face).getData() != (byte)recipe.needed.data.getData()) neededMatch = false;
			} 
		} else {
			Log.high("Null needed material, skipping.");
			neededMatch = true; // null material needed
		}
		return neededMatch;
	}

	public void run() {
		SyncProcess();
	}
	

	private void gatherLoadedChunks() {
		OtherGrowth.gatheredChunks.clear();

		for (World world : Bukkit.getServer().getWorlds()) {
			if (world.getLoadedChunks().length > 0) {
				Log.high("World: "+world.toString());
				int count = 0;
				for (Chunk chunk : world.getLoadedChunks()) {
					OtherGrowth.gatheredChunks.add(chunk.getChunkSnapshot());
					count++;
				}
				Log.high("Chunks gathered: "+count+", World: "+world.toString()+": "+world.getLoadedChunks().length);
			}
		}
	}

	private void gatherChunks() {
		OtherGrowth.gatheredChunks.clear();

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

							OtherGrowth.gatheredChunks.add(currentChunkSnapShot);
						}
					}
				}
			}
		}
	}

}
