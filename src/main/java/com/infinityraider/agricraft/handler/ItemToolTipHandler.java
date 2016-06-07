package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.api.v1.IClipper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.items.ItemClipping;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
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

		// Test if stack is good.
		if (StackHelper.isValid(ItemClipping.class, stack)) {
			if (StackHelper.hasTag(stack)) {
				stack = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
			} else {
				// Passed bad clipping item.
				return;
			}
		}

		// Abort if stack is bad.
		if (!StackHelper.isValid(stack)) {
			return;
		}

		// Add Seed Information.
		if (CropPlantHandler.isValidSeed(stack) && stack.hasTagCompound()) {
			PlantStats stats = new PlantStats(stack.getTagCompound());
			if (stats.isAnalyzed()) {
				stats.addStats(event.getToolTip());
			} else {
				event.getToolTip().add(" " + I18n.translateToLocal("agricraft_tooltip.unidentified"));
			}
		}
	}

	/**
	 * Adds tooltips to items that are trowels (implementing ITrowel)
	 */
	@SubscribeEvent
	public void addTrowelTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (StackHelper.isValid(ITrowel.class, stack)) {
			ITrowel trowel = (ITrowel) stack.getItem();
			if (trowel.hasSeed(stack)) {
				ItemStack seed = trowel.getSeed(stack);
				event.getToolTip().add(I18n.translateToLocal("agricraft_tooltip.seed") + ": " + seed.getItem().getItemStackDisplayName(seed));
			} else {
				event.getToolTip().add(I18n.translateToLocal("agricraft_tooltip.trowel"));
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
			event.getToolTip().add(I18n.translateToLocal("agricraft_tooltip.clipper1"));
			event.getToolTip().add(I18n.translateToLocal("agricraft_tooltip.clipper2"));
			event.getToolTip().add(I18n.translateToLocal("agricraft_tooltip.clipper3"));
		}
	}

}
