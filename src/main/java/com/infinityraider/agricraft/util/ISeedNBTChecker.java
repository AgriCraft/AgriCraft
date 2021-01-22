package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.item.ItemStack;

public interface ISeedNBTChecker {
    ISeedNBTChecker CHECKER = new ISeedNBTChecker() {};

    default boolean doTagsMatch(ItemStack seed, ItemStack test) {
        if(seed.getItem() instanceof ItemDynamicAgriSeed) {
            return seed.hasTag()
                    && test.hasTag()
                    // TODO: This could be governed by the genome instead
                    && seed.getTag().contains(AgriNBT.PLANT)
                    && test.getTag().contains(AgriNBT.PLANT)
                    && seed.getTag().getString(AgriNBT.PLANT).equals(test.getTag().getString(AgriNBT.PLANT));
        }
        // TODO
        return true;
    }
}
