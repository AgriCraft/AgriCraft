package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SlotSeedAnalyzerSeed extends Slot{
    public SlotSeedAnalyzerSeed(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(stack!=null && stack.stackSize>0 && stack.getItem()!=null && stack.getItem() instanceof ItemSeeds) {
            ItemSeeds seed = (ItemSeeds) stack.getItem();
            if(!SeedHelper.isValidSeed(seed, stack.getItemDamage())) {
                return false;
            }
            if(stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if(tag.hasKey(Names.NBT.analyzed)) {
                    return !tag.getBoolean(Names.NBT.analyzed);
                }
            }
            return true;
        }

        return false;
    }
}
