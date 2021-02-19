package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.ItemGrate;
import com.infinityraider.agricraft.content.tools.ItemClipper;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.content.tools.ItemRake;
import com.infinityraider.agricraft.content.tools.ItemTrowel;
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

    public final ItemClipper clipper;
    public final ItemMagnifyingGlass magnifying_glass;
    public final ItemRake rake_wood;
    public final ItemRake rake_iron;
    public final ItemTrowel trowel;

    public final ItemAgriNugget nugget_diamond;
    public final ItemAgriNugget nugget_emerald;
    public final ItemAgriNugget nugget_quartz;

    public final ItemGrate grate;

    private AgriItemRegistry() {
        this.debugger = new ItemDebugger();

        this.crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new ItemSeedAnalyzer();
        this.journal = new ItemJournal();

        this.seed = new ItemDynamicAgriSeed();

        this.clipper = new ItemClipper();
        this.magnifying_glass = new ItemMagnifyingGlass();
        this.rake_wood = new ItemRake(ItemRake.WOOD_LOGIC);
        this.rake_iron = new ItemRake(ItemRake.IRON_LOGIC);
        this.trowel = new ItemTrowel();

        this.nugget_diamond = new ItemAgriNugget(Names.Nuggets.DIAMOND);
        this.nugget_emerald = new ItemAgriNugget(Names.Nuggets.EMERALD);
        this.nugget_quartz = new ItemAgriNugget(Names.Nuggets.QUARTZ);

        this.grate = new ItemGrate();
    }

}
