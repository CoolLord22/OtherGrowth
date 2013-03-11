package com.gmail.zariust.mcplugins.othergrowth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherGrowth extends JavaPlugin implements Listener {
    @Override
	public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    @Override
	public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome to OtherGrowth, " + event.getPlayer().getDisplayName() + "!");
    }
}

