package com.gmail.zariust.mcplugins.othergrowth;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

public class Recipe {
	public Material needed;
	public Material target;
	public Material replacementMat;
	public Biome biome;
	public Double chance;
    public Map<String, Boolean> worlds = new HashMap<String, Boolean>();
    
	public static Recipe parseFrom(String name, ConfigurationSection node) {
		Log.high("Parsing recipe ("+name+").");// keys:"+node.getKeys(true).toString());
		if (node == null) return null;
		//String regionName = node.getString("region");
		Recipe recipe = new Recipe();
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

}
