/*
 */
package com.infinityraider.agricraft.crafting;

import com.google.gson.JsonObject;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import java.util.Optional;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Root class for all shaped custom wood recipes.
 */
public class CustomWoodShapedRecipe extends ShapedOreRecipe {

    public CustomWoodShapedRecipe(ResourceLocation id, ItemStack result, CraftingHelper.ShapedPrimer primer) {
        super(id, result, primer);
    }

    @Override
    public boolean matches(InventoryCrafting ic, World world) {
        return super.matches(ic, world) && inferMaterial(ic).isPresent();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic) {
        final ItemStack result = super.getCraftingResult(ic);
        final Optional<CustomWoodType> material = inferMaterial(ic);
        if (material.isPresent()) {
            final NBTTagCompound tag = StackHelper.getTag(result);
            material.get().writeToNBT(tag);
            result.setTagCompound(tag);
        }
        return result;
    }

    public Optional<CustomWoodType> inferMaterial(InventoryCrafting ic) {
        for (int r = 0; r < ic.getWidth(); r++) {
            for (int c = 0; c < ic.getHeight(); c++) {
                final ItemStack stack = ic.getStackInRowAndColumn(r, c);
                final Optional<CustomWoodType> material = CustomWoodTypeRegistry.getFromStack(stack);
                if (material.isPresent()) {
                    return material;
                }
            }
        }
        return Optional.empty();
    }

    public static final class Factory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            ShapedOreRecipe fake = ShapedOreRecipe.factory(context, json);
            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = fake.getRecipeWidth();
            primer.height = fake.getRecipeHeight();
            primer.input = fake.getIngredients();
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true); // Hack
            return new CustomWoodShapedRecipe(fake.getRegistryName(), fake.getRecipeOutput(), primer);
        }

    }
}
