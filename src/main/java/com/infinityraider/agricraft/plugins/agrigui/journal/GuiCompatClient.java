package com.infinityraider.agricraft.plugins.agrigui.journal;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiCompatClient {

	public static void openScreen(PlayerEntity player, Hand hand) {
		Minecraft.getInstance().displayGuiScreen(new JournalScreen(new TranslationTextComponent("journalgui"), player, hand));
	}

}
