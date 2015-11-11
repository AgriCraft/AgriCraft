package com.InfinityRaider.AgriCraft.api.v2;

import net.minecraft.item.ItemStack;

/**
 * Implement in tools that should have rake functionality
 */
public interface IRake {
    boolean removeWeeds(ICrop crop, ItemStack rake);
}
