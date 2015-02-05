package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class NBTHelper {
    public static NBTTagCompound getMaterialTag(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        if(stack!=null && stack.getItem()!=null) {
            tag.setString(Names.NBT.material, Block.blockRegistry.getNameForObject(((ItemBlock) stack.getItem()).field_150939_a));
            tag.setInteger(Names.NBT.materialMeta, stack.getItemDamage());
        }
        return tag;
    }

    public static boolean listContainsStack(NBTTagList list, ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return true;
        }
        for(int i=0;i<list.tagCount();i++) {
            NBTTagCompound tagAtIndex = list.getCompoundTagAt(i);
            ItemStack stackAtIndex = ItemStack.loadItemStackFromNBT(tagAtIndex);
            if(stack.getItem()==stackAtIndex.getItem() && stack.getItemDamage()==stackAtIndex.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    public static void addCoordsToNBT(int x, int y, int z, NBTTagCompound tag) {
        tag.setInteger(Names.NBT.x, x);
        tag.setInteger(Names.NBT.y, y);
        tag.setInteger(Names.NBT.z, z);
    }

    public static int[] getCoordsFromNBT(NBTTagCompound tag) {
        int[] coords = null;
        if(tag.hasKey(Names.NBT.x) && tag.hasKey(Names.NBT.y) && tag.hasKey(Names.NBT.z)) {
            coords = new int[] {tag.getInteger(Names.NBT.x), tag.getInteger(Names.NBT.y), tag.getInteger(Names.NBT.z)};
        }
        return coords;
    }

    public static void sortStacks(NBTTagList list) {
        //if the list has no or one stack, nothing has to be sorted
        if(list.tagCount()<2) {
            return;
        }
        //sort the stacks in a new array
        ItemStack[] array = new ItemStack[list.tagCount()];
        ItemStack first;
        int index;
        for(int i=0;i<array.length;i++) {
            index = 0;
            first = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(index));
            //find the next stack in the list
            for(int j=0;j<list.tagCount();j++) {
                ItemStack stackAtIndex = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(j));
                String firstName = first.getDisplayName().equalsIgnoreCase("Seeds")?"Wheat Seeds":first.getDisplayName();
                String stackAtIndexName = stackAtIndex.getDisplayName().equalsIgnoreCase("Seeds")?"Wheat Seeds":stackAtIndex.getDisplayName();
                if(firstName.compareToIgnoreCase(stackAtIndexName)>0) {
                    first = stackAtIndex;
                    index = j;
                }
            }
            //set the stack at this index to the next stack in order
            array[i] = first;
            //remove the stack at this index from the list
            list.removeTag(index);
        }
        //add all the stacks in order to the list
        for(ItemStack stack:array) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            list.appendTag(tag);
        }
    }
}
