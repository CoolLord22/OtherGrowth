// Log.java - Bukkit Plugin Logger Wrapper
// Copyright (C) 2012 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.gmail.zariust.mcplugins.othergrowth;


public class Log {

	/**
	 * logInfo - display a log message with a standard prefix
	 * 
	 * @param msg Message to be displayed
	 */
	private static void logInfo(String msg) {
		OtherGrowth.log.info("["+OtherGrowth.pluginName+":"+OtherGrowth.pluginVersion+"] "+msg);
	}

	/**
	 * logWarning - display a warning log message with a standard prefix
	 * 
	 * @param msg Message to be displayed
	 */
	static void logWarning(String msg) {
		OtherGrowth.log.warning("["+OtherGrowth.pluginName+":"+OtherGrowth.pluginVersion+"] "+msg);
	}


	public static void warning(String msg) {
		logWarning(msg);
	}

	public static void low(String msg) {
		if (OtherGrowthConfig.getVerbosity().exceeds(Verbosity.LOW)) logInfo(msg);
	}

	public static void normal(String msg) {
		if (OtherGrowthConfig.getVerbosity().exceeds(Verbosity.NORMAL)) logInfo(msg);
	}

	public static void high(String msg) {
		if (OtherGrowthConfig.getVerbosity().exceeds(Verbosity.HIGH)) logInfo(msg);
	}

	public static void highest(String msg) {
		if (OtherGrowthConfig.getVerbosity().exceeds(Verbosity.HIGHEST)) logInfo(msg);
	}

	public static void extreme(String msg) {
		if (OtherGrowthConfig.getVerbosity().exceeds(Verbosity.EXTREME)) logInfo(msg);
	}

	// TODO: This is only for temporary debug purposes.
	public static void stackTrace() {
		if(OtherGrowthConfig.getVerbosity().exceeds(Verbosity.EXTREME)) Thread.dumpStack();
	}

	public static void dMsg(String msg) {
		if (OtherGrowthConfig.verbosity.exceeds(Verbosity.HIGHEST)) logInfo(msg);
	}
}
