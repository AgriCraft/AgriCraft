package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.items.ItemAgriSeed;
import com.infinityraider.agricraft.items.core.ItemClipper;
import com.infinityraider.agricraft.items.core.ItemClipping;
import com.infinityraider.agricraft.items.core.ItemCropSticks;
import com.infinityraider.agricraft.items.core.ItemDebugger;
import com.infinityraider.agricraft.items.core.ItemJournal;
import com.infinityraider.agricraft.items.core.ItemMagnifyingGlass;
import com.infinityraider.agricraft.items.ItemNugget;
import com.infinityraider.agricraft.items.core.ItemRake;
import com.infinityraider.agricraft.items.core.ItemTrowel;
import com.infinityraider.infinitylib.item.ItemBase;
import javax.annotation.Nonnull;

public class AgriItems {

    private static final AgriItems INSTANCE = new AgriItems();

    public static AgriItems getInstance() {
        return INSTANCE;
    }

    private AgriItems() {
        crop_sticks = new ItemCropSticks();
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

    @Nonnull
    public final ItemBase crop_sticks;
    @Nonnull
    public final ItemBase journal;
    @Nonnull
    public final ItemBase trowel;
    @Nonnull
    public final ItemBase debugger;
    @Nonnull
    public final ItemBase hand_rake;
    @Nonnull
    public final ItemBase clipper;
    @Nonnull
    public final ItemBase clipping;
    @Nonnull
    public final ItemBase seed;
    @Nonnull
    public final ItemBase nugget;
    @Nonnull
    public final ItemBase magnifying_glass;

}
