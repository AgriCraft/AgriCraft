package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import net.minecraft.item.Item;

/**
 * Tool to uproot weeds.
 * Comes in a wooden and iron variant.
 */
public class ItemHandRake extends Item {

    public ItemHandRake() {
        setCreativeTab(AgriCraftTab.agriCraftTab);
        maxStackSize = 1;
    }
}
