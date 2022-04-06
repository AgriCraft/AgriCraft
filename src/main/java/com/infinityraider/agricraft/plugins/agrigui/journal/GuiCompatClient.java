package com.infinityraider.agricraft.plugins.agrigui.journal;

import com.infinityraider.agricraft.plugins.agrigui.GuiPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class GuiCompatClient {

	public static void openScreen(Player player, InteractionHand hand) {
		Minecraft.getInstance().setScreen(new JournalScreen(GuiPlugin.TITLE_JOURNAL, player, hand));
	}

}
