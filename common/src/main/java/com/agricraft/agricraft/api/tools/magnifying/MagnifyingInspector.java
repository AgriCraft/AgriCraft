package com.agricraft.agricraft.api.tools.magnifying;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

/**
 * An inspector produce an inspectable that will be used to add components to the magnifying tooltip
 */
public interface MagnifyingInspector {

	/**
	 * Determine if something looked by the player is inspectable
	 * @param level the level the player is in
	 * @param player the player
	 * @param hitResult the hit result of the player
	 * @return an optional containing the inspectable if any was found
	 */
	Optional<MagnifyingInspectable> inspect(Level level, Player player, HitResult hitResult);

}
