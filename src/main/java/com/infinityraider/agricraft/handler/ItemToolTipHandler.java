package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriTrowelItem;

import java.text.MessageFormat;
import java.util.Collection;

import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ItemToolTipHandler {

    private static final ItemToolTipHandler INSTANCE = new ItemToolTipHandler();

    public static ItemToolTipHandler getInstance() {
        return INSTANCE;
    }

    private ItemToolTipHandler() {}

    private static void addFormatted(ItemTooltipEvent event, String format, Object... objects) {
        event.getToolTip().add(new StringTextComponent(MessageFormat.format(format, objects)).mergeStyle(TextFormatting.DARK_AQUA));
    }

    private static void addCategory(ItemTooltipEvent event, String category) {
        event.getToolTip().add(new StringTextComponent(category + ":").mergeStyle(TextFormatting.DARK_AQUA));
    }

    private static void addParameter(ItemTooltipEvent event, String key, Object value) {
        event.getToolTip().add(new StringTextComponent(" - " + key + ": " + value).mergeStyle(TextFormatting.DARK_AQUA));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addRegistryInfo(ItemTooltipEvent event) {
        if (AgriCraft.instance.getConfig().registryTooltips()) {
            final Item item = event.getItemStack().getItem();
            addCategory(event, "Registry");
            addParameter(event, "id", item.getRegistryName());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addNbtInfo(ItemTooltipEvent event) {
        if (AgriCraft.instance.getConfig().nbtTooltips()) {
            addCategory(event, "NBT");
            if (event.getItemStack().hasTag()) {
                final CompoundNBT tag = event.getItemStack().getTag();
                for (String key : tag.keySet()) {
                    addParameter(event, key, tag.get(key));
                }
            } else {
                addFormatted(event, " - No NBT Data");
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addTagInfo(ItemTooltipEvent event) {
        if (AgriCraft.instance.getConfig().tagTooltips()) {
            addCategory(event, "Item Tags");
            Collection<ResourceLocation> itemTags = ItemTags.getCollection().getOwningTags(event.getItemStack().getItem());
            if(itemTags.size() > 0) {
                itemTags.forEach(tag -> addFormatted(event, " - " + tag.toString()));
            } else {
                addFormatted(event, " - No Item Tags");
            }
            if(event.getItemStack().getItem() instanceof BlockItem) {
                addCategory(event, "Block Tags");
                Collection<ResourceLocation> blockTags =
                        BlockTags.getCollection().getOwningTags(((BlockItem) event.getItemStack().getItem()).getBlock());
                if(blockTags.size() > 0) {
                    blockTags.forEach(tag -> addFormatted(event, " - " + tag.toString()));
                } else {
                    addFormatted(event, " - No Block Tags");
                }
            }
        }
    }

    /**
     * Adds tooltips to soils.
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addSoilInfo(ItemTooltipEvent event) {
        AgriApi.getSoilRegistry().valueOf(event.getItemStack()).ifPresent(soil -> soil.addDisplayInfo(text -> event.getToolTip().add(text)));
    }

    /**
     * Adds tooltips to items that are journals (implementing ITrowel).
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addJournalTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof IAgriJournalItem) {
            IAgriJournalItem journal = (IAgriJournalItem) stack.getItem();
            int count = journal.getDiscoveredSeeds(stack).size();
            event.getToolTip().add(new StringTextComponent("" + count + " ").appendSibling(AgriToolTips.JOURNAL_SEEDS));
            event.getToolTip().add(AgriToolTips.JOURNAL_USE_1);
            event.getToolTip().add(AgriToolTips.JOURNAL_USE_2);
        }
    }

    /**
     * Adds tooltips to items that are trowels (implementing ITrowel).
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addTrowelTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof IAgriTrowelItem) {
            event.getToolTip().add(AgriToolTips.TROWEL);
            IAgriTrowelItem trowel = (IAgriTrowelItem) stack.getItem();
            trowel.getGenome(stack).map(genome -> {
                event.getToolTip().add(AgriToolTips.getPlantTooltip(genome.getPlant()));
                trowel.getGrowthStage(stack).ifPresent(stage -> event.getToolTip().add(AgriToolTips.getGrowthTooltip(stage)));
                return genome.getStats();
            }).ifPresent(stats -> stats.addTooltips(text -> event.getToolTip().add(text)));
        }
    }

    /**
     * Adds tooltips to items that are clippers (implementing IAgriClipperItem).
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addClipperTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IAgriClipperItem) {
            event.getToolTip().add(AgriToolTips.CLIPPER);
        }
    }

    /**
     * Adds tooltips to items that are rakes (implementing IAgriRakeItem).
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addRakeTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IAgriRakeItem) {
            event.getToolTip().add(AgriToolTips.RAKE);
        }
    }

    /**
     * Adds tooltips to the seed bag (we must do this here to avoid the enchantment tool tip)
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void addSeedBagTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof ItemSeedBag) {
            ItemSeedBag bag = (ItemSeedBag) stack.getItem();
            ItemSeedBag.Contents contents = bag.getContents(stack);
            if(bag.isActivated(stack)) {
                // Remove previous tooltip
                if(event.getToolTip().size() > 0) {
                    ITextComponent name = event.getToolTip().get(0);
                    event.getToolTip().clear();
                    event.getToolTip().add(name);
                }
                // Description
                event.getToolTip().add(AgriToolTips.SEED_BAG_ACTIVE);
                event.getToolTip().add(AgriToolTips.EMPTY_LINE);
                // Contents
                if(contents.getPlant().isPlant()) {
                    event.getToolTip().add(new StringTextComponent("")
                            .appendSibling(AgriToolTips.SEED_BAG_CONTENTS)
                            .appendSibling(new StringTextComponent(" " + contents.getCount() + " "))
                            .appendSibling(contents.getPlant().getSeedName()));
                } else {
                    event.getToolTip().add(AgriToolTips.SEED_BAG_EMPTY);
                }
                // Sorter
                event.getToolTip().add(new StringTextComponent("")
                        .appendSibling(AgriToolTips.SEED_BAG_SORTER)
                        .appendSibling(new StringTextComponent(" "))
                        .appendSibling(contents.getSorter().getName()));
                event.getToolTip().add(AgriToolTips.EMPTY_LINE);
                // Usage
                if(ModuleKeyboard.getInstance().isKeyPressed(Minecraft.getInstance().gameSettings.keyBindSneak)) {
                    event.getToolTip().add(new StringTextComponent("")
                            .mergeStyle(TextFormatting.DARK_GRAY).appendSibling(AgriToolTips.SEED_BAG_MAIN_HAND));
                    event.getToolTip().add(new StringTextComponent("")
                            .mergeStyle(TextFormatting.DARK_GRAY).appendSibling(AgriToolTips.SEED_BAG_OFF_HAND));
                    event.getToolTip().add(new StringTextComponent("")
                            .mergeStyle(TextFormatting.DARK_GRAY).appendSibling(AgriToolTips.SEED_BAG_SCROLLING));
                } else {
                    event.getToolTip().add(new StringTextComponent("")
                            .mergeStyle(TextFormatting.DARK_GRAY).appendSibling(AgriToolTips.SNEAK_INFO));
                }
            } else {
                event.getToolTip().add(AgriToolTips.SEED_BAG_INACTIVE_1);
                event.getToolTip().add(AgriToolTips.SEED_BAG_INACTIVE_2);
            }
        }
    }

}
