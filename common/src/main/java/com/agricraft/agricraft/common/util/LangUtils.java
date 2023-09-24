package com.agricraft.agricraft.common.util;

import net.minecraft.network.chat.Component;

public class LangUtils {

	public static Component plantName(String plantId) {
		return Component.translatable("plant.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

	public static Component seedName(String plantId) {
		return Component.translatable("seed.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

	public static Component soilName(String soilId) {
		return Component.translatable("soil.agricraft." + soilId.replace(":", ".").replace("/", "."));
	}

	public static Component plantDescription(String plantId) {
		return Component.translatable("description.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

}
