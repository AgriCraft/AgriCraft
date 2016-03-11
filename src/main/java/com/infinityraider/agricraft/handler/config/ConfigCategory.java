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
	WORLDGEN("world"),
	IRRIGATION("irrigation"),
	STORAGE("storage"),
	DECORATION("decoration"),
	COMPATIBILITY("compatibility"),
	CLIENT("client");
	
	public final String name;

	private ConfigCategory(String name) {
		this.name = name;
	}
	
}
