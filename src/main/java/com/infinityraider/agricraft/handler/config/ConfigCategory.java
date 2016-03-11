/*
 * 
 */
package com.infinityraider.agricraft.handler.config;

/**
 *
 * @author RlonRyan
 */
public enum ConfigCategory {
	
	CORE("core"),
	FARMING("farming"),
	TOOLS("tools"),
	DEBUG("debug"),
	WORLDGEN("world generation"),
	IRRIGATION("irrigation"),
	STORAGE("storage"),
	DECORATION("decoration"),
	COMPATIBILITY("compatibility"),
	CLIENT("clientside");
	
	public final String name;

	private ConfigCategory(String name) {
		this.name = name;
	}
	
}
