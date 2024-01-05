package com.agricraft.agricraft.common.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * A menu provider that can be used to send extra data to the client.
 */
public interface ExtraDataMenuProvider extends MenuProvider {

	/**
	 * Writes the extra data to the buffer to be sent to the client when the menu is opened.
	 *
	 * @param player The player that is opening the menu.
	 * @param buffer The buffer to write the data to.
	 */
	void writeExtraData(ServerPlayer player, FriendlyByteBuf buffer);

}
