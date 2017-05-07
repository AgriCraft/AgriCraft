package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.item.ItemStack;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.items.IAgriTrowelItem;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ItemToolTipHandler {

    private static final ItemToolTipHandler INSTANCE = new ItemToolTipHandler();

    public static ItemToolTipHandler getInstance() {
        return INSTANCE;
    }

    private ItemToolTipHandler() {
    }

    /**
     * Adds tooltips for SEED stats.
     *
     * @param event
     */
    @SubscribeEvent
    public void addSeedStatsTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        // Add Seed Information.
        if (stack != null) {
            AgriSeed seed = SeedRegistry.getInstance().valueOf(stack).orElse(null);
            if (seed != null) {
                if (seed.getStat().isAnalyzed()) {
                    seed.getStat().addStats(event.getToolTip());
                } else {
                    event.getToolTip().add(" " + AgriCore.getTranslator().translate("agricraft_tooltip.unidentified"));
                }
            }
        }
    }
    
    @SubscribeEvent
    public void addNbtInfo(ItemTooltipEvent event) {
        if (AgriCraftConfig.enableNBTTooltips) {
            event.getToolTip().add(ChatFormatting.DARK_AQUA + "NBT:");
            if (StackHelper.hasTag(event.getItemStack())) {
                final NBTTagCompound tag = StackHelper.getTag(event.getItemStack());
                for (String key : tag.getKeySet()) {
                    event.getToolTip().add(ChatFormatting.DARK_AQUA + " - " + key + ": " + tag.getTag(key).toString());
                }
            } else {
                event.getToolTip().add(ChatFormatting.DARK_AQUA + " - No NBT Tags");
            }
        }
    }

    @SubscribeEvent
    public void addOreDictInfo(ItemTooltipEvent event) {
        if (AgriCraftConfig.enableOreDictTooltips) {
            event.getToolTip().add(ChatFormatting.DARK_AQUA + "OreDict:");
            final int[] ids = OreDictionary.getOreIDs(event.getItemStack());
            for (int id : ids) {
                event.getToolTip().add(ChatFormatting.DARK_AQUA + " - " + OreDictionary.getOreName(id) + " (" + id + ")");
            }
            if (ids.length == 0) {
                event.getToolTip().add(ChatFormatting.DARK_AQUA + " - No OreDict Entries");
            }
        }
    }

    /**
     * Adds tooltips to items that are trowels (implementing ITrowel).
     *
     * @param event
     */
    @SubscribeEvent
    public void addTrowelTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack != null && stack.getItem() instanceof IAgriTrowelItem) {
            AgriSeed seed = SeedRegistry.getInstance().valueOf(event.getItemStack()).orElse(null);
            if (seed != null) {
                event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.seed") + ": " + seed.getPlant().getSeedName());
            } else {
                event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.trowel"));
            }
        }
    }

    /**
     * Adds tooltips to items that are clippers (implementing IClipper).
     *
     * @param event
     */
    @SubscribeEvent
    public void addClipperTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (StackHelper.isValid(stack, IAgriClipperItem.class)) {
            event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper1"));
            event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper2"));
            event.getToolTip().add(AgriCore.getTranslator().translate("agricraft_tooltip.clipper3"));
        }
    }

}
