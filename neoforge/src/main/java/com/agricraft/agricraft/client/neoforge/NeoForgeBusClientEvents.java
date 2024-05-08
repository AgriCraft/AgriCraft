package com.agricraft.agricraft.client.neoforge;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.item.SeedBagItem;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
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


	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !player.isShiftKeyDown() || !player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.SEED_BAG.get())) {
			return;
		}
		event.setCanceled(true);
		SeedBagItem.changeSorter(player.getItemInHand(InteractionHand.MAIN_HAND), (int) event.getScrollDeltaY());
		int s = player.getItemInHand(InteractionHand.MAIN_HAND).getOrCreateTag().getInt("sorter");
		String id = SeedBagItem.SORTERS.get(s).getId().toString().replace(":", ".");
		player.displayClientMessage(Component.translatable("agricraft.tooltip.bag.sorter")
				.append(Component.translatable("agricraft.tooltip.bag.sorter." + id)), true);
	}

}
