package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.content.core.CropStickVariant;
import com.infinityraider.agricraft.content.core.ItemCropSticks;
import com.infinityraider.agricraft.content.core.ItemDebugger;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.Item;

public class AgriItemRegistry {

    private static final AgriItemRegistry INSTANCE = new AgriItemRegistry();

    public static AgriItemRegistry getInstance() {
        return INSTANCE;
    }

    private AgriItemRegistry() {
        crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);
        journal = new ItemJournal();
        trowel = new ItemTrowel();
        debugger = new ItemDebugger();
        hand_rake = new ItemRake();
        clipper = new ItemClipper();
        clipping = new ItemClipping();
        seed = new ItemAgriSeed();
        nugget = new ItemNugget();
        magnifying_glass = new ItemMagnifyingGlass();
    }

    public final Item crop_sticks_wood;
    public final Item crop_sticks_iron;
    public final Item crop_sticks_obsidian;

    public final ItemBase journal;

    public final ItemBase trowel;

    public final ItemBase hand_rake;

    public final ItemBase magnifying_glass;

    public final ItemBase clipper;
    public final ItemBase clipping;

    public final ItemBase seed;

    public final ItemBase nugget;

    public final ItemBase debugger;

}
