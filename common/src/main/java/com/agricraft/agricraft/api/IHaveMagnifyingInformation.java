package com.agricraft.agricraft.api;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface IHaveMagnifyingInformation {

	default boolean addToMagnifyingGlassTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

}
