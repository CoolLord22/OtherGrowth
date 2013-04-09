package com.gmail.zariust.mcplugins.othergrowth;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.zariust.mcplugins.othergrowth.common.CommonMaterial;
import com.gmail.zariust.mcplugins.othergrowth.common.Log;
import com.gmail.zariust.mcplugins.othergrowth.common.OtherMat;
import com.gmail.zariust.mcplugins.othergrowth.common.data.SimpleData;

public class Recipe {
	public String name;
	public OtherMat needed;
	public OtherMat target;
	public OtherMat replacementMat;
	public Biome biome;
	public Double chance;
    public Map<String, Boolean> worlds = new HashMap<String, Boolean>();
	public Map<String, Boolean> regions = new HashMap<String, Boolean>();;
    
	public static Recipe parseFrom(String name, ConfigurationSection node) {
		Log.high("Parsing recipe ("+name+").");// keys:"+node.getKeys(true).toString());
		if (node == null) {
			Log.dMsg("Parse failed - node is null.");
			return null;
		}
		Recipe recipe = new Recipe();
		recipe.name = name;
		recipe.regions = parseRegionsFrom(node, null);
		//recipe.biome = node.getString("biome");

		recipe.needed = getOtherMat(node.getString("needed"));
		recipe.target = getOtherMat(node.getString("target"));

		
		recipe.replacementMat = getOtherMat(node.getString("replacement"));
		// Replacement mat needs a data value so make 0 if not specified
		if (recipe.replacementMat.data == null) recipe.replacementMat.data = new SimpleData(0);
		
		recipe.chance = node.getDouble("chance");
		recipe.worlds = OtherGrowthConfig.parseWorldsFrom(node, null);
		
		if (recipe.target == null || recipe.replacementMat == null) {
			Log.warning("Error: material to replace or replacewith is null, not changing anything.");
		}

		Log.high("Recipe loaded - needed: "+recipe.needed.toString()+" replacement: "+recipe.replacementMat.toString()+" target: "+recipe.target.toString());
		return recipe;
	}

	private static OtherMat getOtherMat(String matString) {
        OtherMat omat = new OtherMat();
        
        String[] split = matString.split("@");
        omat.id = CommonMaterial.matchMaterial(split[0]);
        if (omat.id == null) return omat;
        
        if (split.length > 1) omat.data = SimpleData.parse(omat.id, split[1]);
        
        return omat;
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
