package com.InfinityRaider.AgriCraft.items.crafting;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeTank implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
        ItemStack wood = invCrafting.getStackInSlot(0);
        if(wood==null || !OreDictHelper.hasOreId(wood, "plankWood")) {
            return false;           //stack is not wood
        }
        for(int i=1;i<invCrafting.getSizeInventory();i++) {
            ItemStack stackAtIndex = invCrafting.getStackInSlot(i);
            if (i == 1 || i == 4) {
                if (stackAtIndex!=null) {
                    return false;   //stack in slot 1 or 4
                }
            }
            else if (stackAtIndex==null || stackAtIndex.getItem()==null) {
                return false;       //no stack in slot
            }
            else if(wood.getItem()!=stackAtIndex.getItem() || wood.getItemDamage()!=stackAtIndex.getItemDamage()) {
                return false;       //stack is not the same as the rest
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        ItemStack result = null;
        if(invCrafting.getStackInSlot(0)!=null && invCrafting.getStackInSlot(0).getItem()!=null) {
            if(OreDictHelper.hasOreId(invCrafting.getStackInSlot(0), "plankWood")) {
                result = new ItemStack(Blocks.blockWaterTank,1,0);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString(Names.material, Block.blockRegistry.getNameForObject(((ItemBlock)invCrafting.getStackInSlot(0).getItem()).field_150939_a));
                tag.setInteger(Names.materialMeta, invCrafting.getStackInSlot(0).getItemDamage());
                result.stackTagCompound = tag;
            }
        }
        return result;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
