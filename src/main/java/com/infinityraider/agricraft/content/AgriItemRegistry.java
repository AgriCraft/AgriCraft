package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.core.*;
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

    public final ItemBase seed;

    public final ItemBase nugget_diamond;
    public final ItemBase nugget_emerald;
    public final ItemBase nugget_quartz;

    /*
    public final ItemBase journal;

    public final ItemBase trowel;

    public final ItemBase hand_rake;

    public final ItemBase magnifying_glass;

    public final ItemBase clipper;
     */

    private AgriItemRegistry() {
        this.debugger = new ItemDebugger();

        crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);

        this.seed = new ItemDynamicAgriSeed();

        this.nugget_diamond = new ItemAgriNugget(Names.Nuggets.DIAMOND);
        this.nugget_emerald = new ItemAgriNugget(Names.Nuggets.EMERALD);
        this.nugget_quartz = new ItemAgriNugget(Names.Nuggets.QUARTZ);

        /*
        journal = new ItemJournal();
        trowel = new ItemTrowel();
        debugger = new ItemDebugger();
        hand_rake = new ItemRake();
        clipper = new ItemClipper();
        magnifying_glass = new ItemMagnifyingGlass();
         */
    }

}
