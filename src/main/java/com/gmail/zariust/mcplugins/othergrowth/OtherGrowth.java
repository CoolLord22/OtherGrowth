package com.gmail.zariust.mcplugins.othergrowth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class OtherGrowth extends JavaPlugin implements Listener {
	private static Server server;
	static OtherGrowth plugin;
	public static String pluginName;
	public static String pluginVersion;  
	boolean pluginEnabled;
	
	static OtherGrowthConfig config;
	static Logger log;
	protected static Random rng;    
	
	static int syncTaskId = 0;
	static int aSyncTaskId = 0;

	final static Map<World, Set<ChunkSnapshot>> gatheredChunks = new HashMap<World, Set<ChunkSnapshot>>();

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
		config.load(); // load config, Dependencies & enable
	};

	public static void enableOtherGrowth() {
		// async - runs every x ticks, gathers chunks to check and compares blocks against recipies			
		RunAsync aSyncRunner = new RunAsync(OtherGrowth.plugin);
//		aSyncTaskId = server.getScheduler().scheduleAsyncRepeatingTask(OtherGrowth.plugin, aSyncRunner, OtherGrowthConfig.taskDelay, OtherGrowthConfig.taskDelay);                     
		aSyncTaskId = server.getScheduler().scheduleSyncRepeatingTask(OtherGrowth.plugin, aSyncRunner, OtherGrowthConfig.taskDelay, OtherGrowthConfig.taskDelay);                     

		// sync - runs every x ticks and actually makes the changes?
		//	        RunSync syncRunner = new RunSync();
		//	        syncTaskId = server.getScheduler().scheduleSyncRepeatingTask(OtherGrowth.plugin, syncRunner, OtherGrowthConfig.taskDelay+10, OtherGrowthConfig.taskDelay);                     

		plugin.pluginEnabled = true;
	}

	public static void disableOtherGrowth() {
		server.getScheduler().cancelTask(syncTaskId);
		server.getScheduler().cancelTask(aSyncTaskId);
		plugin.pluginEnabled = false;
	}

	@Override
	public void onDisable() {
		// Stop any running scheduler tasks
		//server.getScheduler().cancelAllTasks();  // does this cancel only this plugins tasks?
		server.getScheduler().cancelTask(syncTaskId);
		server.getScheduler().cancelTask(aSyncTaskId);
	};
}
