package com.gmail.zariust.mcplugins.othergrowth.common;

import org.bukkit.Material;

import com.gmail.zariust.mcplugins.othergrowth.common.data.Data;

public class OtherMat {
    public Material      id;
    public Data          data;
    
    @Override
    public String toString() {
        return id.toString() + "@"+ (data==null?"ANY":data.get(id));
    }
}
