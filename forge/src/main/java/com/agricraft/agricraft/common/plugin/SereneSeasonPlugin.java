package com.agricraft.agricraft.common.plugin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.requirement.AgriSeason;

public class SereneSeasonPlugin {

	public static void init() {
		AgriApi.getSeasonLogic().claim(SereneSeasonPlugin.class, (level, blockPos) -> AgriSeason.ANY);
	}

	public String modid() {
		return "sereneseasons";
	}

	public String description() {
		return "serene seasons compatibility";
	}

}
