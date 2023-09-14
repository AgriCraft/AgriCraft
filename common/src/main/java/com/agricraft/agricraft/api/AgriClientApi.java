package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspector;
import com.agricraft.agricraft.client.gui.MagnifyingGlassOverlay;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

/**
 * The AgriCraft Client API v2
 */
public final class AgriClientApi {

	/**
	 * Add a magnifying inspector
	 * @param inspector the inspector
	 */
	public static void addMagnifyingInspector(MagnifyingInspector inspector) {
		MagnifyingGlassOverlay.addInspector(inspector);
	}

	/**
	 * Add a predicate to allow the overlay to render
	 * @param predicate the predicate
	 */
	public static void addMagnifyingAllowingPredicate(Predicate<Player> predicate) {
		MagnifyingGlassOverlay.addAllowingPredicate(predicate);
	}

}
