package com.agricraft.agricraft.common.plugin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.requirement.AgriSeason;

public class FabricSeasonPlugin {

	public static void init() {
		AgriApi.getSeasonLogic().claim(FabricSeasonPlugin.class, (level, blockPos) -> AgriSeason.ANY);
	}

	public String modid() {
		return "fabricseasons";
	}

	public String description() {
		return "fabric seasons compatibility";
	}

}
