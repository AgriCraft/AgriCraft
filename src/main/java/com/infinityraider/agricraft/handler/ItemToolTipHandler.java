package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.utility.StackHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.MessageFormat;
import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        if (!stack.isEmpty()) {
            AgriSeed seed = AgriApi.getSeedRegistry().valueOf(stack).orElse(null);
            if (seed != null) {
                if (seed.getStat().isAnalyzed()) {
                    seed.getStat().addStats(event.getToolTip()::add);
                } else {
                    event.getToolTip().add(" " + AgriCore.getTranslator().translate("agricraft_tooltip.unidentified"));
                }
            }
        }
    }

//    @SubscribeEvent
//    public void addProductInfo(ItemTooltipEvent event) {
//        if (StackHelper.hasTag(event.getItemStack())) {
//            final NBTTagCompound tag = StackHelper.getTag(event.getItemStack());
//            if (tag.hasKey(IAgriHarvestProduct.PRODUCT_MARKER_TAG)) {
//                final int minAmount = tag.getInteger(IAgriHarvestProduct.PRODUCT_MIN_TAG);
//                final int maxAmount = tag.getInteger(IAgriHarvestProduct.PRODUCT_MAX_TAG);
//                final double chance = tag.getDouble(IAgriHarvestProduct.PRODUCT_CHANCE_TAG);
//                event.getToolTip().add(ChatFormatting.GRAY + "Chance: " + DecimalFormat.getPercentInstance().format(chance));
//                event.getToolTip().add(ChatFormatting.GRAY + "Min. Amount: " + minAmount);
//                event.getToolTip().add(ChatFormatting.GRAY + "Max. Amount: " + maxAmount);
//            }
//        }
//    }
    private static void addFormatted(ItemTooltipEvent event, String format, Object... objects) {
        event.getToolTip().add(ChatFormatting.DARK_AQUA + MessageFormat.format(format, objects));
    }

    private static void addCategory(ItemTooltipEvent event, String category) {
        event.getToolTip().add(ChatFormatting.DARK_AQUA + category + ":");
    }

    private static void addParameter(ItemTooltipEvent event, String key, Object value) {
        event.getToolTip().add(ChatFormatting.DARK_AQUA + " - " + key + ": " + Objects.toString(value));
    }

    @SubscribeEvent
    public void addRegistryInfo(ItemTooltipEvent event) {
        if (AgriCraftConfig.enableRegistryTooltips) {
            final Item item = event.getItemStack().getItem();
            addCategory(event, "Registry");
            addParameter(event, "id", item.getRegistryName());
        }
    }

    @SubscribeEvent
    public void addNbtInfo(ItemTooltipEvent event) {
        if (AgriCraftConfig.enableNBTTooltips) {
            addCategory(event, "NBT");
            if (StackHelper.hasTag(event.getItemStack())) {
                final NBTTagCompound tag = StackHelper.getTag(event.getItemStack());
                for (String key : tag.getKeySet()) {
                    addParameter(event, key, tag.getTag(key));
                }
            } else {
                addFormatted(event, " - No NBT Tags");
            }
        }
    }

    @SubscribeEvent
    public void addOreDictInfo(ItemTooltipEvent event) {
        if (AgriCraftConfig.enableOreDictTooltips && !event.getItemStack().isEmpty()) {
            addCategory(event, "OreDict");
            final int[] ids = OreDictionary.getOreIDs(event.getItemStack());
            for (int id : ids) {
                addFormatted(event, " - {1} ({0})", id, OreDictionary.getOreName(id));
            }
            if (ids.length == 0) {
                addFormatted(event, " - No OreDict Entries");
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
        if (!stack.isEmpty() && stack.getItem() instanceof IAgriTrowelItem) {
            AgriSeed seed = AgriApi.getSeedRegistry().valueOf(event.getItemStack()).orElse(null);
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
