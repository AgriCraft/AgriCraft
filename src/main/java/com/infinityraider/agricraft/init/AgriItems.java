package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.items.ItemAgriSeed;
import com.infinityraider.agricraft.items.ItemClipper;
import com.infinityraider.agricraft.items.ItemClipping;
import com.infinityraider.agricraft.items.ItemCrop;
import com.infinityraider.agricraft.items.ItemDebugger;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.items.ItemMagnifyingGlass;
import com.infinityraider.agricraft.items.ItemNugget;
import com.infinityraider.agricraft.items.ItemRake;
import com.infinityraider.agricraft.items.ItemTrowel;
import com.infinityraider.infinitylib.item.ItemBase;
import javax.annotation.Nonnull;

public class AgriItems {

    private static final AgriItems INSTANCE = new AgriItems();

    public static AgriItems getInstance() {
        return INSTANCE;
    }

    private AgriItems() {
        CROPS = new ItemCrop();
        JOURNAL = new ItemJournal();
        TROWEL = new ItemTrowel();
        DEBUGGER = new ItemDebugger();
        HAND_RAKE = new ItemRake();
        CLIPPER = new ItemClipper();
        AGRI_CLIPPING = new ItemClipping();
        AGRI_SEED = new ItemAgriSeed();
        AGRI_NUGGET = new ItemNugget();
        MAGNIFYING_GLASS = new ItemMagnifyingGlass();
    }

    @Nonnull
    public final ItemBase CROPS;
    @Nonnull
    public final ItemBase JOURNAL;
    @Nonnull
    public final ItemBase TROWEL;
    @Nonnull
    public final ItemBase DEBUGGER;
    @Nonnull
    public final ItemBase HAND_RAKE;
    @Nonnull
    public final ItemBase CLIPPER;
    @Nonnull
    public final ItemBase AGRI_CLIPPING;
    @Nonnull
    public final ItemBase AGRI_SEED;
    @Nonnull
    public final ItemBase AGRI_NUGGET;
    @Nonnull
    public final ItemBase MAGNIFYING_GLASS;

}
