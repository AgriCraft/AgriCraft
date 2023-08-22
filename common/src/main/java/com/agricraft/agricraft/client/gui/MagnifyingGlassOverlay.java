package com.agricraft.agricraft.client.gui;

import com.agricraft.agricraft.api.IHaveMagnifyingInformation;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MagnifyingGlassOverlay {

	private static int hoverTicks;

	private static final List<Predicate<Player>> allowingPredicates = new ArrayList<>();

	static {
		addAllowingPredicate(player -> player.getMainHandItem().is(ModItems.MAGNIFYING_GLASS.get()));
		addAllowingPredicate(player -> player.getOffhandItem().is(ModItems.MAGNIFYING_GLASS.get()));
		addAllowingPredicate(player -> {
			CompoundTag tag = player.getItemBySlot(EquipmentSlot.HEAD).getTag();
			return tag != null && tag.getBoolean("magnifying");
		});
	}

	public static void addAllowingPredicate(Predicate<Player> predicate) {
		allowingPredicates.add(predicate);
	}


	public static void renderOverlay(GuiGraphics graphics, float partialTicks) {
		// greatly inspired from create goggles
		Minecraft mc = Minecraft.getInstance();
		if ((mc.screen != null && !(mc.screen instanceof ChatScreen)) || mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR || mc.player == null || mc.level == null) {
			hoverTicks = 0;
			return;
		}
		boolean shouldOverlay = false;
		for (Predicate<Player> predicate : allowingPredicates) {
			if (predicate.test(mc.player)) {
				shouldOverlay = true;
				break;
			}
		}
		if (!shouldOverlay) {
			hoverTicks = 0;
			return;
		}
		if (!(mc.hitResult instanceof BlockHitResult)) {
			hoverTicks = 0;
			return;
		}
		BlockHitResult result = (BlockHitResult) mc.hitResult;
		BlockEntity be = mc.level.getBlockEntity(result.getBlockPos());
		if (be instanceof IHaveMagnifyingInformation mgInfo) {
			hoverTicks++;
			int posX = graphics.guiWidth() / 2 + 20 ; //cfg.overlayOffsetX.get();
			int posY = graphics.guiHeight() / 2;// + cfg.overlayOffsetY.get();
			float fade = Mth.clamp(hoverTicks / 48f, 0, 1);  // goes from 0 to 1 in 24 ticks, then stays at 1
			posX += (int) (Math.pow(1 - fade, 3) * 8);
			List<Component> tooltip = new ArrayList<>();
			mgInfo.addToMagnifyingGlassTooltip(tooltip, mc.player.isShiftKeyDown());

			if (!tooltip.isEmpty()) {
				int tooltipHeight = 8;
				if (tooltip.size() > 1) {
					tooltipHeight += 2; // gap between title lines and next lines
					tooltipHeight += (tooltip.size() - 1) * 10;
				}
				RenderSystem.setShaderColor(1, 1, 1, Mth.clamp(hoverTicks / 24f, 0, 0.8f));
				graphics.renderTooltip(mc.font, tooltip, Optional.empty(), posX - TooltipRenderUtil.MOUSE_OFFSET, posY - tooltipHeight/2 + 12);
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}

		} else {
			hoverTicks = 0;
		}
	}

}
