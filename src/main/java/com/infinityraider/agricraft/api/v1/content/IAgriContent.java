package com.infinityraider.agricraft.api.v1.content;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

/**
 * This interface gives access to AgriCraft content added to Minecraft,
 * this includes Blocks, Items, Fluids, Enchantments, etc.
 */
public interface IAgriContent {
    /**
     * @return the AgriCraft IAgriContent instance
     */
    static IAgriContent getInstance() {
        return AgriApi.getAgriContent();
    }

    /** @return the AgriCraft Blocks */
    @SuppressWarnings("unused")
    static IAgriContent.Blocks blocks() {
        return getInstance().getBlocks();
    }

    /**  @return the AgriCraft Enchantments */
    @SuppressWarnings("unused")
    static IAgriContent.Enchantments enchantments() {
        return getInstance().getEnchantments();
    }

    /** @return the AgriCraft Fluids */
    @SuppressWarnings("unused")
    static IAgriContent.Fluids fluids() {
        return getInstance().getFluids();
    }

    /** @return the AgriCraft Items */
    @SuppressWarnings("unused")
    static IAgriContent.Items items() {
        return getInstance().getItems();
    }

    /** @return the AgriCraft Sounds */
    @SuppressWarnings("unused")
    static IAgriContent.Sounds sounds() {
        return getInstance().getSounds();
    }

    /** @return the AgriCraft Sounds */
    @SuppressWarnings("unused")
    static IAgriContent.Tabs tabs() {
        return getInstance().getTabs();
    }

    /**
     * @return Wrapper instance for the Blocks
     */
    IAgriContent.Blocks getBlocks();

    /**
     * @return Wrapper instance for the Enchantments
     */
    IAgriContent.Enchantments getEnchantments();

    /**
     * @return Wrapper instance for the Fluids
     */
    IAgriContent.Fluids getFluids();

    /**
     * @return Wrapper instance for the Items
     */
    IAgriContent.Items getItems();

    /**
     * @return Wrapper instance for the Sounds
     */
    IAgriContent.Sounds getSounds();

    /**
     * @return Wrapper instance for the Tabs
     */
    IAgriContent.Tabs getTabs();

    /**
     * Interface wrapping getters for the AgriCraft blocks
     */
    @SuppressWarnings("unused")
    interface Blocks {
        /** @return the AgriCraft Crop Plant block */
        Block getCropPlantBlock();

        /** @return the AgriCraft Wooden Crop Sticks block */
        Block getWoodCropSticksBlock();
        
        /** @return the AgriCraft Iron Crop Sticks block */
        Block getIronCropSticksBlock();
        
        /** @return the AgriCraft Obsidian Crop Sticks block */
        Block getObsidianCropSticksBlock();

        /** @return the AgriCraft Seed Analyzer block */
        Block getSeedAnalyzerBlock();

        /** @return the AgriCraft Irrigation Tank block */
        Block getTankBlock();

        /** @return the AgriCraft Irrigation Channel block */
        Block getChannelBlock();

        /** @return the AgriCraft Hollow Irrigation Channel block */
        Block getHollowChannelBlock();

        /** @return the AgriCraft Sprinkler block */
        Block getSprinklerBlock();

        /** @return the AgriCraft Grate block */
        Block getGrateBlock();

        /** @return the AgriCraft Greenhouse Air block */
        Block getGreenHouseAirBlock();
    }

    /**
     * Interface wrapping getters for the AgriCraft enchantments
     */
    @SuppressWarnings("unused")
    interface Enchantments {
        /** @return the AgriCraft Seed Bag enchantment */
        Enchantment getSeedBagEnchantment();
    }

    /**
     * Interface wrapping getters for the AgriCraft fluids
     */
    @SuppressWarnings("unused")
    interface Fluids {
        /** @return the AgriCraft Irrigation Tank Water fluid */
        Fluid getTankWater();
    }

    /**
     * Interface wrapping getters for the AgriCraft items
     */
    @SuppressWarnings("unused")
    interface Items {
        /** @return the AgriCraft Debugger Item */
        Item getDebuggerItem();

        /** @return the AgriCraft Wooden Crop Sticks Item */
        Item getWoodCropSticksItem();

        /** @return the AgriCraft Iron Crop Sticks Item */
        Item getIronCropSticksItem();

        /** @return the AgriCraft Obsidian Crop Sticks Item */
        Item getObsidianCropSticksItem();

        /** @return the AgriCraft Seed Analyzer Item */
        Item getSeedAnalyzerItem();

        /** @return the AgriCraft Journal Item */
        Item getJournalItem();

        /** @return the AgriCraft Seed Item */
        Item getSeedItem();

        /** @return the AgriCraft Irrigation Tank Item */
        Item getIrrigationTankItem();

        /** @return the AgriCraft Irrigation Channel Item */
        Item getIrrigationChannelItem();

        /** @return the AgriCraft Hollow Irrigation Channel Item */
        Item getHollowIrrigationChannelItem();

        /** @return the AgriCraft Sprinkler Item */
        Item getSprinklerItem();

        /** @return the AgriCraft Irrigation Valve Item */
        Item getValveItem();

        /** @return the AgriCraft Clipper Item */
        Item getClipperItem();

        /** @return the AgriCraft Magnifying Glass Item */
        Item getMagnifyingGlassItem();

        /** @return the AgriCraft Wooden Rake Item */
        Item getWoodenRakeItem();

        /** @return the AgriCraft Iron Rake Item */
        Item getIronRakeItem();

        /** @return the AgriCraft Trowel Item */
        Item getTrowelItem();

        /** @return the AgriCraft Seed Bag Item */
        Item getSeedBagItem();

        /** @return the AgriCraft Grate Item */
        Item getGrateItem();

        /** @return the AgriCraft Coal Nugget Item (can be null if disabled in the config) */
        @Nullable
        Item getCoalNuggetItem();

        /** @return the AgriCraft Diamond Nugget Item (can be null if disabled in the config) */
        @Nullable
        Item getDiamondNuggetItem();

        /** @return the AgriCraft Emerald Nugget Item (can be null if disabled in the config) */
        @Nullable
        Item getEmeraldNuggetItem();

        /** @return the AgriCraft Quartz Nugget Item (can be null if disabled in the config) */
        @Nullable
        Item getQuartzNuggetItem();
        
    }

    /**
     * Interface wrapping getters for the AgriCraft sound events
     */
    @SuppressWarnings("unused")
    interface Sounds {
        /** @return the AgriCraft Valve sound */
        SoundEvent getValveSound();
    }

    /**
     * Interface wrapping getters for the AgriCraft creative tabs
     */
    @SuppressWarnings("unused")
    interface Tabs {
        /** @return the AgriCraft main item group */
        ItemGroup getAgriCraftTab();

        /** @return the AgriCraft seeds item group */
        ItemGroup getSeedsTab();
    }
    
}
