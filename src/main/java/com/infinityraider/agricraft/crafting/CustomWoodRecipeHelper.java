/*
 */
package com.infinityraider.agricraft.crafting;

import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.agricraft.utility.StackHelper;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * A class to manage the creation of custom wood recipes.
 */
public final class CustomWoodRecipeHelper {

    public static final String MATERIAL_PARAMETER = "MATERIAL_PARAMETER";

    /**
     * Adds the given recipe for every available WOOD type.
     *
     * @param block The block that is the result of the recipe.
     * @param amount The amount of result that will be output.
     * @param params Same as for GameRegistry. The only difference is that
     * planks will get replaced with the different woods.
     * @param shaped If the recipe is shaped.
     */
    public static void registerCustomWoodRecipe(Block block, int amount, boolean shaped, Object... params) {
        CustomWoodTypeRegistry.getAllTypes().forEach(type -> registerCustomWoodRecipeVariant(type, new ItemStack(block, amount), shaped, params));
    }

    /**
     * Adds the given recipe for every available WOOD type.
     *
     * @param result The result of the recipe.
     * @param params Same as for GameRegistry. The only difference is that
     * planks will get replaced with the different woods.
     * @param shaped If the recipe is shaped.
     */
    public static void registerCustomWoodRecipe(ItemStack result, boolean shaped, Object... params) {
        CustomWoodTypeRegistry.getAllTypes().forEach(type -> registerCustomWoodRecipeVariant(type, result, shaped, params));
    }

    /**
     * Registers a custom wood recipe variant for a given material.
     *
     * @param material The material to be used in this specific variant.
     * @param result The result of the recipe.
     * @param shaped If the recipe is shaped or shapeless.
     * @param params The ingredients for the recipe.
     */
    public static void registerCustomWoodRecipeVariant(CustomWoodType material, ItemStack result, boolean shaped, Object... params) {
        // Create a clean copy of the input parameters that we can modify.
        final Object[] ingredients = Arrays.copyOf(params, params.length);

        // Replace the wood type in the recipe template.
        for (int i = 0; i < ingredients.length; i++) {
            // Convert Blocks to itemstacks.
            if (ingredients[i] instanceof Block) {
                ingredients[i] = new ItemStack((Block) ingredients[i]);
            } // Convert Items to itemstacks.
            else if (ingredients[i] instanceof Item) {
                ingredients[i] = new ItemStack((Item) ingredients[i]);
            }
            
            // Test if input matches the material parameter.
            if (Objects.equals(MATERIAL_PARAMETER, ingredients[i])) {
                // Replace the material parameter.
                ingredients[i] = material.getStack();
            } // Test if input is a customwood instance, which needs tag updated.
            else if ((ingredients[i] instanceof ItemStack) /*&& (StackHelper.isValid((ItemStack) ingredients[i], ItemBlockCustomWood.class))*/) {
                final ItemStack stack = (ItemStack) ingredients[i];
                final NBTTagCompound tag = StackHelper.getTag(stack);
                material.writeToNBT(tag);
                stack.setTagCompound(tag);
            }
        }

        // Setup the result.
        final NBTTagCompound tag = StackHelper.getTag(result);
        material.writeToNBT(tag);
        result.setTagCompound(tag);

        // Register the Recipe
        if (shaped) {
            GameRegistry.addShapedRecipe(result, ingredients);
        } else {
            GameRegistry.addShapelessRecipe(result, ingredients);
        }
    }

}
