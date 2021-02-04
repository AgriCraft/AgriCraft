package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.tools.ItemClipper;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.content.tools.ItemRake;
import com.infinityraider.agricraft.content.tools.ItemTrowel;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.Item;

public class AgriItemRegistry {

    private static final AgriItemRegistry INSTANCE = new AgriItemRegistry();

    public static AgriItemRegistry getInstance() {
        return INSTANCE;
    }

    public final ItemBase debugger;

    public final Item crop_sticks_wood;
    public final Item crop_sticks_iron;
    public final Item crop_sticks_obsidian;

    public final Item seed_analyzer;

    public final ItemBase seed;

    public final ItemBase clipper;
    public final ItemBase magnifying_glass;
    public final ItemBase rake_wood;
    public final ItemBase rake_iron;
    public final ItemBase trowel;

    public final ItemBase nugget_diamond;
    public final ItemBase nugget_emerald;
    public final ItemBase nugget_quartz;

    /*
    public final ItemBase journal;
     */

    private AgriItemRegistry() {
        this.debugger = new ItemDebugger();

        this.crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new ItemSeedAnalyzer();

        this.seed = new ItemDynamicAgriSeed();

        this.clipper = new ItemClipper();
        this.magnifying_glass = new ItemMagnifyingGlass();
        this.rake_wood = new ItemRake(ItemRake.WOOD_LOGIC);
        this.rake_iron = new ItemRake(ItemRake.IRON_LOGIC);
        this.trowel = new ItemTrowel();

        this.nugget_diamond = new ItemAgriNugget(Names.Nuggets.DIAMOND);
        this.nugget_emerald = new ItemAgriNugget(Names.Nuggets.EMERALD);
        this.nugget_quartz = new ItemAgriNugget(Names.Nuggets.QUARTZ);

        /*
        journal = new ItemJournal();
         */
    }

}
