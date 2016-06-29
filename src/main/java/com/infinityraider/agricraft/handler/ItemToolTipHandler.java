package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.items.ITrowel;
import com.infinityraider.agricraft.api.v1.items.IClipper;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.item.ItemStack;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ItemToolTipHandler {

	/**
	 * Adds tooltips for SEED stats
	 */
	@SubscribeEvent
	public void addSeedStatsTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		// Add Seed Information.
		if (stack != null) {
			AgriSeed seed = SeedRegistry.getInstance().getSeed(stack);
			if (seed != null) {
				if (seed.getStat().isAnalyzed()) {
					seed.getStat().addStats(event.getToolTip());
				} else {
					event.getToolTip().add(" " + AgriCore.getTranslator().translate("agricraft_tooltip.unidentified"));
				}
			}
		}
	}

	/**
	 * Adds tooltips to items that are trowels (implementing ITrowel)
	 */
	@SubscribeEvent
	public void addTrowelTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (stack != null && stack.getItem() instanceof ITrowel) {
			AgriSeed seed = SeedRegistry.getInstance().getSeed(event.getItemStack());
			if (seed != null) {
				event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + seed.getPlant().getSeedName());
			} else {
				event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.trowel"));
			}
		}
	}

	/**
	 * Adds tooltips to items that are clippers (implementing IClipper)
	 */
	@SubscribeEvent
	public void addClipperTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (StackHelper.isValid(IClipper.class, stack)) {
			event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper1"));
			event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper2"));
			event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper3"));
		}
	}

}
