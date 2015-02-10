package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Tool to uproot weeds.
 * Comes in a wooden and iron variant.
 */
public class ItemHandRake extends Item {

    private static final int WOOD_VARIANT_META = 0;
    private static final int IRON_VARIANT_META = 1;

    public ItemHandRake() {
        setCreativeTab(AgriCraftTab.agriCraftTab);
        maxStackSize = 1;
        hasSubtypes = true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, WOOD_VARIANT_META));
        list.add(new ItemStack(item, 1, IRON_VARIANT_META));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String base = super.getUnlocalizedName(itemStack);
        if (itemStack.getItemDamage() == WOOD_VARIANT_META) {
            return base + ".wood";
        } else if (itemStack.getItemDamage() == IRON_VARIANT_META) {
            return base + ".iron";
        } else {
            throw new IllegalArgumentException("Unsupported meta value of " + itemStack.getItemDamage() + " for ItemHandRake.");
        }
    }
}
