package com.gmail.zariust.mcplugins.othergrowth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class OtherGrowth extends JavaPlugin implements Listener {
	private static Server server;
	static OtherGrowth plugin;
	public static String pluginName, pluginVersion;  
	boolean pluginEnabled;
	
	static OtherGrowthConfig config;
	static Logger log;
	protected static Random rng;    
	
	static BukkitTask changeBlocksTask;
	static BukkitTask scanBlocksTask;

	static Queue<MatchResult> results = new LinkedList<MatchResult>();
	public static Map<String, Set<Recipe>> recipes = new HashMap<String, Set<Recipe>>();
	final static Queue<ChunkSnapshot> gatheredChunks = new LinkedList<ChunkSnapshot>();

	
	public OtherGrowth() {
		rng = new Random();
		log = Logger.getLogger("Minecraft");
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		OtherGrowth.server = getServer();
		OtherGrowth.plugin = this;
		pluginName = this.getDescription().getName();
		pluginVersion = this.getDescription().getVersion();

		this.getCommand("og").setExecutor(new OtherGrowthCommand(this));

		// Load the config files
		OtherGrowth.config = new OtherGrowthConfig(this);
		config.loadFromStartup(); // load config, Dependencies & enable
	};

	public static void enableOtherGrowth() {
		// async - runs every x ticks, gathers chunks to check and compares blocks against recipes			
		RunAsync aSyncRunner = new RunAsync(OtherGrowth.plugin);
		if (OtherGrowthConfig.globalScanAsync) {
			scanBlocksTask = server.getScheduler().runTaskTimerAsynchronously(OtherGrowth.plugin, aSyncRunner, OtherGrowthConfig.taskDelay, OtherGrowthConfig.taskDelay);
		} else {
			scanBlocksTask = server.getScheduler().runTaskTimer(OtherGrowth.plugin, aSyncRunner, OtherGrowthConfig.taskDelay, OtherGrowthConfig.taskDelay);
		}
		
		// sync - runs every x ticks and actually makes the changes?
        RunSync syncRunner = new RunSync(plugin);
        changeBlocksTask = server.getScheduler().runTaskTimer(OtherGrowth.plugin, syncRunner, OtherGrowthConfig.taskDelay+10, OtherGrowthConfig.taskDelay);                     

		plugin.pluginEnabled = true;
	}

	public static void disableOtherGrowth() {
		if (changeBlocksTask != null) changeBlocksTask.cancel();
		if (scanBlocksTask != null) scanBlocksTask.cancel();
		plugin.pluginEnabled = false;
	}

	@Override
	public void onDisable() {
		// Stop any running scheduler tasks
		if (changeBlocksTask != null) changeBlocksTask.cancel();
		if (scanBlocksTask != null) scanBlocksTask.cancel();

	};
}
