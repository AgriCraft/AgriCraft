package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.api.codecs.AgriSoilValue;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.api.stat.AgriStat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LangUtils {

	public static Component plantName(String plantId) {
		return Component.translatable("plant.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

	public static Component plantDescription(String plantId) {
		String id = "description.agricraft." + plantId.replace(":", ".").replace("/", ".");
		MutableComponent component = Component.translatable(id);
		if (component.getString().equals(id)) {
			return null;
		}
		return component;
	}

	public static Component weedName(String weedId) {
		return Component.translatable("weed.agricraft." + weedId.replace(":", ".").replace("/", "."));
	}

	public static Component seedName(String plantId) {
		return Component.translatable("seed.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

	public static Component soilName(String soilId) {
		return Component.translatable("soil.agricraft." + soilId.replace(":", ".").replace("/", "."));
	}

	public static Component soilPropertyName(String property, AgriSoilValue value) {
		return Component.translatable("agricraft.soil." + property + "." + value.name().toLowerCase());
	}

	public static MutableComponent statName(AgriStat stat) {
		return Component.translatable("agricraft.stat." + stat.getId());
	}

	public static Component seasonName(AgriSeason season) {
		return Component.translatable("agricraft.season." + season.name().toLowerCase());
	}

}
