package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.*;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.tools.*;
import com.infinityraider.agricraft.reference.Names;

import javax.annotation.Nullable;

public final class AgriItemRegistry {
    public static final IAgriContent.Items ACCESSOR = new Accessor();

    public static final ItemDebugger DEBUGGER = new ItemDebugger();

    public static final ItemCropSticks CROP_STICKS_WOOD = new ItemCropSticks(CropStickVariant.WOOD);
    public static final ItemCropSticks CROP_STICKS_IRON = new ItemCropSticks(CropStickVariant.IRON);
    public static final ItemCropSticks CROP_STICKS_OBSIDIAN = new ItemCropSticks(CropStickVariant.OBSIDIAN);

    public static final ItemSeedAnalyzer SEED_ANALYZER = new ItemSeedAnalyzer();
    public static final ItemJournal JOURNAL = new ItemJournal();

    public static final ItemDynamicAgriSeed SEED = new ItemDynamicAgriSeed();

    public static final ItemIrrigationTank TANK = new ItemIrrigationTank();
    public static final ItemIrrigationChannel CHANNEL = new ItemIrrigationChannel();
    public static final ItemIrrigationChannelHollow CHANNEL_HOLLOW = new ItemIrrigationChannelHollow();
    public static final ItemSprinkler SPRINKLER = new ItemSprinkler();
    public static final ItemChannelValve VALVE = new ItemChannelValve();

    public static final ItemClipper CLIPPER = new ItemClipper();
    public static final ItemMagnifyingGlass MAGNIFYING_GLASS = new ItemMagnifyingGlass();
    public static final ItemRake RAKE_WOOD = new ItemRake(ItemRake.WOOD_LOGIC);
    public static final ItemRake RAKE_IRON = new ItemRake(ItemRake.IRON_LOGIC);
    public static final ItemTrowel TROWEL = new ItemTrowel();
    public static final ItemSeedBag SEED_BAG = new ItemSeedBag();

    public static final ItemAgriNugget NUGGET_COAL = AgriCraft.instance.getConfig().enableCoalNugget()
            ? new ItemAgriNugget.Burnable(Names.Nuggets.COAL)
            : null;
    public static final ItemAgriNugget NUGGET_DIAMOND = AgriCraft.instance.getConfig().enableDiamondNugget()
            ? new ItemAgriNugget(Names.Nuggets.DIAMOND)
            : null;
    public static final ItemAgriNugget NUGGET_EMERALD = AgriCraft.instance.getConfig().enableEmeraldNugget()
            ? new ItemAgriNugget(Names.Nuggets.EMERALD)
            : null;
    public static final ItemAgriNugget NUGGET_QUARTZ = AgriCraft.instance.getConfig().enableQuartzNugget()
            ? new ItemAgriNugget(Names.Nuggets.QUARTZ)
            : null;

    public static final ItemGrate grate = new ItemGrate();
    
    private static final class Accessor implements IAgriContent.Items {
        private Accessor() {
        }

        @Override
        public ItemDebugger getDebuggerItem() {
            return DEBUGGER;
        }

        @Override
        public ItemCropSticks getWoodCropSticksItem() {
            return CROP_STICKS_WOOD;
        }

        @Override
        public ItemCropSticks getIronCropSticksItem() {
            return CROP_STICKS_IRON;
        }

        @Override
        public ItemCropSticks getObsidianCropSticksItem() {
            return CROP_STICKS_OBSIDIAN;
        }

        @Override
        public ItemSeedAnalyzer getSeedAnalyzerItem() {
            return SEED_ANALYZER;
        }

        @Override
        public ItemJournal getJournalItem() {
            return JOURNAL;
        }

        @Override
        public ItemDynamicAgriSeed getSeedItem() {
            return SEED;
        }

        @Override
        public ItemIrrigationTank getIrrigationTankItem() {
            return TANK;
        }

        @Override
        public ItemIrrigationChannel getIrrigationChannelItem() {
            return CHANNEL;
        }

        @Override
        public ItemIrrigationChannelHollow getHollowIrrigationChannelItem() {
            return CHANNEL_HOLLOW;
        }

        @Override
        public ItemSprinkler getSprinklerItem() {
            return SPRINKLER;
        }

        @Override
        public ItemChannelValve getValveItem() {
            return VALVE;
        }

        @Override
        public ItemClipper getClipperItem() {
            return CLIPPER;
        }

        @Override
        public ItemMagnifyingGlass getMagnifyingGlassItem() {
            return MAGNIFYING_GLASS;
        }

        @Override
        public ItemRake getWoodenRakeItem() {
            return RAKE_WOOD;
        }

        @Override
        public ItemRake getIronRakeItem() {
            return RAKE_IRON;
        }

        @Override
        public ItemTrowel getTrowelItem() {
            return TROWEL;
        }

        @Override
        public ItemSeedBag getSeedBagItem() {
            return SEED_BAG;
        }

        @Override
        public ItemGrate getGrateItem() {
            return grate;
        }

        @Nullable
        @Override
        public ItemAgriNugget getCoalNuggetItem() {
            return NUGGET_COAL;
        }

        @Nullable
        @Override
        public ItemAgriNugget getDiamondNuggetItem() {
            return NUGGET_DIAMOND;
        }

        @Nullable
        @Override
        public ItemAgriNugget getEmeraldNuggetItem() {
            return NUGGET_EMERALD;
        }

        @Nullable
        @Override
        public ItemAgriNugget getQuartzNuggetItem() {
            return NUGGET_QUARTZ;
        }
    }
}
