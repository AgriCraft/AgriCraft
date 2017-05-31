/*
 */
package com.infinityraider.agricraft.crafting;

import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

/**
 * Root class for all shaped custom wood recipes.
 */
public class CustomWoodShapedRecipe implements IRecipe {

    final ItemStack result;
    final FullRecipeLayout layout;

    public CustomWoodShapedRecipe(ItemStack result, FullRecipeLayout layout) {
        this.result = result;
        this.layout = layout;
    }

    @Override
    public boolean matches(InventoryCrafting ic, World world) {
        final Optional<CustomWoodType> material = inferMaterial(ic);
        if (!material.isPresent()) {
            return false;
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final FuzzyStack expected = layout.get(r, c);
                final ItemStack input = ic.getStackInRowAndColumn(r, c);
                final Optional<CustomWoodType> inputMaterial = CustomWoodTypeRegistry.getFromStack(input);
                if (inputMaterial.isPresent() && !material.equals(inputMaterial)) {
                    return false;
                } else if (!Objects.equals(input, expected)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic) {
        final Optional<CustomWoodType> material = inferMaterial(ic);
        if (material.isPresent()) {
            final ItemStack instance = result.copy();
            final NBTTagCompound tag = StackHelper.getTag(instance);
            material.get().writeToNBT(tag);
            instance.setTagCompound(tag);
            return instance;
        } else {
            return null;
        }
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting ic) {
        return ForgeHooks.defaultRecipeGetRemainingItems(ic);
    }

    public Optional<CustomWoodType> inferMaterial(InventoryCrafting ic) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                final ItemStack stack = ic.getStackInRowAndColumn(r, c);
                final Optional<CustomWoodType> material = CustomWoodTypeRegistry.getFromStack(stack);
                if (material.isPresent()) {
                    return material;
                }
            }
        }
        return Optional.empty();
    }

}
