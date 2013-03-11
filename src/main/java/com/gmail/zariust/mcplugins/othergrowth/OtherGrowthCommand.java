// OtherGrowth - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.mcplugins.othergrowth;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class OtherGrowthCommand implements CommandExecutor {
	private enum OBCommand {
		RELOAD("reload", "r"),
		SETTINGS("settings", "st"),
		DISABLE("disable,disabled,off", "o"),
		ENABLE("enable,enabled,on", "e");
		private String cmdName;
		private String cmdShort;

		private OBCommand(String name, String abbr) {
			cmdName = name;
			cmdShort = abbr;
		}
		
		public static OBCommand match(String label, String firstArg) {
			boolean arg = false;
			if(label.equalsIgnoreCase("og")) arg = true;
			for(OBCommand cmd : values()) {
				if(arg) {
					for (String item : cmd.cmdName.split(",")) {
						if (firstArg.equalsIgnoreCase(item)) return cmd;
					}
				}
				else if(label.equalsIgnoreCase("og" + cmd.cmdShort) || label.equalsIgnoreCase("og" + cmd.cmdName))
					return cmd;
			}
			return null;
		}

		public String[] trim(String[] args, StringBuffer name) {
			if(args.length == 0) return args;
			if(!args[0].equalsIgnoreCase(cmdName)) return args;
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			if(name != null) name.append(" " + args[0]);
			return newArgs;
		}
	}
	private final OtherGrowth parent;
	
	public OtherGrowthCommand(OtherGrowth plugin) {
		parent = plugin;
	}
	
	private String getName(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) return "CONSOLE";
		else if(sender instanceof Player) return ((Player) sender).getName();
		else return "UNKNOWN";
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		OBCommand cmd = OBCommand.match(label, args.length >= 1 ? args[0] : "");
		if(cmd == null) return false;
		StringBuffer cmdName = new StringBuffer(label);
		args = cmd.trim(args, cmdName);
		switch(cmd) {
		case RELOAD:
			if(Dependencies.hasPermission(sender, "OtherGrowth.admin.reloadconfig")) {
				OtherGrowth.config.load();
				sender.sendMessage("OtherGrowth config reloaded.");
				Log.normal("Config reloaded by " + getName(sender) + ".");
			} else sender.sendMessage("You don't have permission to reload the config.");
			break;
		case SETTINGS:
			if(Dependencies.hasPermission(sender, "OtherGrowth.admin.profiling")) {
				sender.sendMessage("OtherGrowth settings:");
				sender.sendMessage((parent.pluginEnabled ? ChatColor.GREEN+"OtherGrowth enabled." : ChatColor.RED+"OtherGrowth disabled."));
				sender.sendMessage("Verbosity: "+ChatColor.GRAY+OtherGrowthConfig.getVerbosity());				
				sender.sendMessage("TickDelay: "+ChatColor.GRAY+OtherGrowthConfig.taskDelay);				
				sender.sendMessage("ChunkScanRadius: "+ChatColor.GRAY+OtherGrowthConfig.globalChunkScanRadius);				
			} else sender.sendMessage("You don't have permission for this command.");
			
			break;
		case ENABLE:
			if(Dependencies.hasPermission(sender, "OtherGrowth.admin.enabledisable")) {
				if (!parent.pluginEnabled) {
					OtherGrowth.enableOtherGrowth();
					sender.sendMessage(ChatColor.GREEN+"OtherGrowth enabled.");
				} else {
					sender.sendMessage(ChatColor.GRAY+"OtherGrowth is already enabled.");
				}
			} else sender.sendMessage("You don't have permission for this command.");
			break;
		case DISABLE:
			if(Dependencies.hasPermission(sender, "OtherGrowth.admin.enabledisable")) {
				if (parent.pluginEnabled) {
					OtherGrowth.disableOtherGrowth();
					sender.sendMessage(ChatColor.RED+"OtherGrowth disabled.");
				} else {
					sender.sendMessage(ChatColor.GRAY+"OtherGrowth is already disabled.");
				}
			} else sender.sendMessage("You don't have permission for this command.");
			break;
		default:
			break;

		}
		return true;
	}
}
