package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.item.ItemStack;

/**
 * Implement in tools that should have rake functionality
 */
public interface IRake {
    boolean removeWeeds(ICrop crop, ItemStack rake);
}
