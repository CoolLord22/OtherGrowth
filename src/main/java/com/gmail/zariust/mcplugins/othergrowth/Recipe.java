package com.gmail.zariust.mcplugins.othergrowth;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

public class Recipe {
	public String name;
	public Material needed;
	public Material target;
	public Material replacementMat;
	public Biome biome;
	public Double chance;
    public Map<String, Boolean> worlds = new HashMap<String, Boolean>();
	public Map<String, Boolean> regions = new HashMap<String, Boolean>();;
    
	public static Recipe parseFrom(String name, ConfigurationSection node) {
		Log.high("Parsing recipe ("+name+").");// keys:"+node.getKeys(true).toString());
		if (node == null) return null;
		Recipe recipe = new Recipe();
		recipe.name = name;
		recipe.regions = parseRegionsFrom(node, null);
		//recipe.biome = node.getString("biome");
		String needString = node.getString("needed");
		if (needString != null) recipe.needed = Material.matchMaterial(needString);
		String targetString = node.getString("target");
		if (targetString != null) recipe.target = Material.matchMaterial(targetString);
		recipe.replacementMat = Material.matchMaterial(node.getString("replacement"));
		recipe.chance = node.getDouble("chance");
		recipe.worlds = OtherGrowthConfig.parseWorldsFrom(node, null);
		
		if (recipe.target == null || recipe.replacementMat == null) {
			Log.warning("Error: material to replace or replacewith is null, not changing anything.");
		}

		return recipe;
	}

	// TODO: refactor parseWorldsFrom, Regions & Biomes as they are all very similar - (beware - fragile, breaks easy)
	private static Map<String, Boolean> parseRegionsFrom(ConfigurationSection node, Map<String, Boolean> def) {
		List<String> regions = OtherGrowthConfig.getMaybeList(node, "region", "regions");
		List<String> regionsExcept = OtherGrowthConfig.getMaybeList(node, "regionexcept", "regionsexcept");
		if(regions.isEmpty() && regionsExcept.isEmpty()) return def;
		Map<String, Boolean> result = new HashMap<String,Boolean>();
		for(String name : regions) {
			if(name.startsWith("-")) {
				result.put(name, false);  // deliberately including the "-" sign
			} else result.put(name, true);
		}
		for(String name : regionsExcept) {
			result.put(name, false);
		}
		if(result.isEmpty()) return null;
		return result;
	}
}
