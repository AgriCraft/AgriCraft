package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.ItemGrate;
import com.infinityraider.agricraft.content.irrigation.ItemIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.ItemIrrigationChannelHollow;
import com.infinityraider.agricraft.content.irrigation.ItemIrrigationTank;
import com.infinityraider.agricraft.content.irrigation.ItemSprinkler;
import com.infinityraider.agricraft.content.tools.*;
import com.infinityraider.agricraft.reference.Names;

public class AgriItemRegistry {

    private static final AgriItemRegistry INSTANCE = new AgriItemRegistry();

    public static AgriItemRegistry getInstance() {
        return INSTANCE;
    }

    public final ItemDebugger debugger;

    public final ItemCropSticks crop_sticks_wood;
    public final ItemCropSticks crop_sticks_iron;
    public final ItemCropSticks crop_sticks_obsidian;

    public final ItemSeedAnalyzer seed_analyzer;
    public final ItemJournal journal;

    public final ItemDynamicAgriSeed seed;

    public final ItemIrrigationTank tank;
    public final ItemIrrigationChannel channel;
    public final ItemIrrigationChannelHollow channel_hollow;
    public final ItemSprinkler sprinkler;

    public final ItemClipper clipper;
    public final ItemMagnifyingGlass magnifying_glass;
    public final ItemRake rake_wood;
    public final ItemRake rake_iron;
    public final ItemTrowel trowel;
    public final ItemSeedBag seed_bag;

    public final ItemAgriNugget nugget_coal;
    public final ItemAgriNugget nugget_diamond;
    public final ItemAgriNugget nugget_emerald;
    public final ItemAgriNugget nugget_quartz;

    public final ItemGrate grate;

    @SuppressWarnings("deprecation")
    private AgriItemRegistry() {
        this.debugger = new ItemDebugger();

        this.crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new ItemSeedAnalyzer();
        this.journal = new ItemJournal();

        this.seed = new ItemDynamicAgriSeed();

        this.tank = new ItemIrrigationTank();
        this.channel = new ItemIrrigationChannel();
        this.channel_hollow = new ItemIrrigationChannelHollow();
        this.sprinkler = new ItemSprinkler();

        this.clipper = new ItemClipper();
        this.magnifying_glass = new ItemMagnifyingGlass();
        this.rake_wood = new ItemRake(ItemRake.WOOD_LOGIC);
        this.rake_iron = new ItemRake(ItemRake.IRON_LOGIC);
        this.trowel = new ItemTrowel();
        this.seed_bag = new ItemSeedBag();

        this.nugget_coal =new ItemAgriNugget.Burnable(Names.Nuggets.COAL);
        this.nugget_diamond = new ItemAgriNugget(Names.Nuggets.DIAMOND);
        this.nugget_emerald = new ItemAgriNugget(Names.Nuggets.EMERALD);
        this.nugget_quartz = new ItemAgriNugget(Names.Nuggets.QUARTZ);

        this.grate = new ItemGrate();
    }
}
