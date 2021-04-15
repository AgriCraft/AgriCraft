package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;

public class ItemAgriNugget extends ItemBase {
    public ItemAgriNugget(String name) {
        super(Names.Items.NUGGET + "_" + name, new Properties().group(ItemGroup.MATERIALS));
    }

    public static class Burnable extends ItemAgriNugget {
        public Burnable(String name) {
            super(name);
        }

        @Override
        @SuppressWarnings("deprecation")
        public int getBurnTime(ItemStack itemStack) {
            return AbstractFurnaceTileEntity.getBurnTimes().get(Items.COAL)/9;
        }

    }
}
