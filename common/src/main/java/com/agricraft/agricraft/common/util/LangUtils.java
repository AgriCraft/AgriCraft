package com.agricraft.agricraft.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LangUtils {

	public static MutableComponent plantName(String plantId) {
		return Component.translatable("plant.agricraft." + plantId.replace(":", ".").replace("/", "."));
	}

}
