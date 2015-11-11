package com.InfinityRaider.AgriCraft.api.v2;

import net.minecraft.item.ItemStack;

public interface ITrowel extends com.InfinityRaider.AgriCraft.api.v1.ITrowel {
    /** Gets the stats from the seed */
    @Override
    ISeedStats getStats(ItemStack trowel) ;
}
