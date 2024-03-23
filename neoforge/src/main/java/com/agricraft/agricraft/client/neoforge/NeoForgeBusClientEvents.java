package com.agricraft.agricraft.client.neoforge;

import com.agricraft.agricraft.api.AgriApi;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

/**
 * NeoForge client event handler in the forge event bus
 */
@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NeoForgeBusClientEvents {

	@SubscribeEvent
	public static void onTooltipRender(RenderTooltipEvent.GatherComponents event) {
		if (event.getItemStack().hasTag() && event.getItemStack().getTag().getBoolean("magnifying")) {
			event.getTooltipElements().add(1, Either.left(Component.translatable("agricraft.tooltip.magnifying").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC)));
		}
	}

}
