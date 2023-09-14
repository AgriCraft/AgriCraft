package com.agricraft.agricraft.client.gui;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspector;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Display a tooltip in the world when holding a magnifying glass or having a magnifying helmet equipped.
 * The tooltip shows information about the genome of the plant.
 * <br>
 * You can register a new predicate to allow the tooltip to be rendered with {@link #addAllowingPredicate}
 * You can add text to the tooltip by implementing the interface {@link MagnifyingInspectable}
 */
public class MagnifyingGlassOverlay {

	private static int hoverTicks;

	private static final List<Predicate<Player>> allowingPredicates = new ArrayList<>();
	private static final Set<MagnifyingInspector> inspectors = new HashSet<>();

	static {
		addAllowingPredicate(player -> player.getMainHandItem().is(ModItems.MAGNIFYING_GLASS.get()));
		addAllowingPredicate(player -> player.getOffhandItem().is(ModItems.MAGNIFYING_GLASS.get()));
		addAllowingPredicate(player -> {
			CompoundTag tag = player.getItemBySlot(EquipmentSlot.HEAD).getTag();
			return tag != null && tag.getBoolean("magnifying");
		});

		inspectors.add((level, player, hitResult) -> {
			if (hitResult instanceof BlockHitResult result) {
				if (level.getBlockEntity(result.getBlockPos()) instanceof MagnifyingInspectable inspectable) {
					return Optional.of(inspectable);
				}
			Optional<AgriSoil> soil = AgriApi.getSoil(level, result.getBlockPos(), level.registryAccess());
				if (soil.isPresent()) {
					return Optional.of(soil.get());
				}
			}
			return Optional.empty();
		});
	}

	/**
	 * Add a predicate to allow the overlay to render
	 *
	 * @param predicate the predicate
	 */
	public static void addAllowingPredicate(Predicate<Player> predicate) {
		allowingPredicates.add(predicate);
	}

	public static void addInspector(MagnifyingInspector inspector) {
		inspectors.add(inspector);
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
		Optional<MagnifyingInspectable> inspectable = inspectors.stream().map(inspectors -> inspectors.inspect(mc.level, mc.player, mc.hitResult))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		if (inspectable.isEmpty()) {
			hoverTicks = 0;
			return;
		}

		hoverTicks++;
		int posX = graphics.guiWidth() / 2 + 20; //cfg.overlayOffsetX.get();
		int posY = graphics.guiHeight() / 2;// + cfg.overlayOffsetY.get();
		float fade = Mth.clamp(hoverTicks / 48f, 0, 1);  // goes from 0 to 1 in 48 ticks, then stays at 1
		posX += (int) (Math.pow(1 - fade, 3) * 8);
		List<Component> tooltip = new ArrayList<>();
		inspectable.get().addMagnifyingTooltip(tooltip, mc.player.isShiftKeyDown());

		if (!tooltip.isEmpty()) {
			int tooltipHeight = 8;
			if (tooltip.size() > 1) {
				tooltipHeight += 2; // gap between title lines and next lines
				tooltipHeight += (tooltip.size() - 1) * 10;
			}
			RenderSystem.setShaderColor(1, 1, 1, Mth.clamp(hoverTicks / 24f, 0, 0.8f));
			graphics.renderTooltip(mc.font, tooltip, Optional.empty(), posX - TooltipRenderUtil.MOUSE_OFFSET, posY - tooltipHeight / 2 + 12);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}

	}

}
