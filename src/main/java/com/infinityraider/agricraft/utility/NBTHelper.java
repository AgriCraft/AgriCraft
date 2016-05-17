package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.reference.AgriCraftNBT;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class NBTHelper {
    public static NBTTagCompound getMaterialTag(ItemStack stack) {
        NBTTagCompound tag = null;
        if(stack!=null && stack.getItem()!=null) {
			Block block = (((ItemBlock) stack.getItem()).block);
            if(block != null) {
                tag = new NBTTagCompound();
                tag.setString(AgriCraftNBT.MATERIAL, block.getRegistryName().toString());
                tag.setInteger(AgriCraftNBT.MATERIAL_META, stack.getMetadata());
            }
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
            if(stackAtIndex==null || stackAtIndex.getItem()==null) {
                continue;
            }
            if(stack.getItem()==stackAtIndex.getItem() && stack.getItemDamage()==stackAtIndex.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    public static void addCoordsToNBT(int[] coords, NBTTagCompound tag) {
        if(coords!=null && coords.length==3) {
            addCoordsToNBT(coords[0], coords[1], coords[2], tag);
        }
    }

    public static void addCoordsToNBT(int x, int y, int z, NBTTagCompound tag) {
        tag.setInteger(AgriCraftNBT.X1, x);
        tag.setInteger(AgriCraftNBT.Y1, y);
        tag.setInteger(AgriCraftNBT.Z1, z);
    }

    public static int[] getCoordsFromNBT(NBTTagCompound tag) {
        int[] coords = null;
        if(tag.hasKey(AgriCraftNBT.X1) && tag.hasKey(AgriCraftNBT.Y1) && tag.hasKey(AgriCraftNBT.Z1)) {
            coords = new int[] {tag.getInteger(AgriCraftNBT.X1), tag.getInteger(AgriCraftNBT.Y1), tag.getInteger(AgriCraftNBT.Z1)};
        }
        return coords;
    }

    public static void sortStacks(NBTTagList list) {
        //clear empty tags from the list
        clearEmptyStacksFromNBT(list);
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

    public static void clearEmptyStacksFromNBT(NBTTagList list) {
        for(int i=list.tagCount()-1;i>=0;i--) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
            if(stack==null || stack.getItem()==null) {
                list.removeTag(i);
            }
        }
    }
}
