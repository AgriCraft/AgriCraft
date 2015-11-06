package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Recipes;
import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@ZenClass("mods.agricraft.CustomWood")
public class CustomWood {

    @ZenMethod
    public static void addShaped(IItemStack output, IItemStack[][] inputs) {
        addRecipe(output, inputs, true);
    }

    @ZenMethod
    public static void addShapeless(IItemStack output, IItemStack[] inputs) {
        addRecipe(output, new IItemStack[][] {inputs}, false);
    }

    private static void addRecipe(IItemStack output, IItemStack[][] inputs, boolean shaped) {
        if (ConfigurationHandler.disableIrrigation) {
            MineTweakerAPI.logError("Irrigation system disabled. Modifications to CustomWood recipes pointless.");
            return;
        }

        ItemStack outputStack = MineTweakerMC.getItemStack(output);
        if (outputStack.getItem() instanceof ItemBlockCustomWood) {
            if (shaped && inputs.length != 3) {
                MineTweakerAPI.logError("Unable to add recipe with input rows other than 3");
                return;
            }

            List<ItemStack> inputsConverted = new ArrayList<ItemStack>();
            for (IItemStack[] input : inputs) {
                for (IItemStack stack : input) {
                    inputsConverted.add(MineTweakerMC.getItemStack(stack));
                }
            }

            MineTweakerAPI.apply(new AddRecipeAction(outputStack, inputsConverted.toArray(new ItemStack[inputsConverted.size()]), shaped));
        } else {
            MineTweakerAPI.logError(outputStack.getDisplayName() + " is not of type ItemBlockCustomWood.");
        }
    }

    @ZenMethod
    public static void remove(IItemStack output) {
        ItemStack itemStack = MineTweakerMC.getItemStack(output);
        if (itemStack.getItem() instanceof ItemBlockCustomWood) {
            MineTweakerAPI.apply(new RemoveRecipeAction(itemStack));
        } else {
            MineTweakerAPI.logError(itemStack.getDisplayName() + " is not of type ItemBlockCustomWood.");
        }
    }

    private static class AddRecipeAction implements IUndoableAction {

        private final ItemStack outputStack;
        private final ItemStack[] inputs;
        private final boolean shaped;

        public AddRecipeAction(ItemStack outputStack, ItemStack[] inputs, boolean shaped) {
            this.outputStack = outputStack;
            this.inputs = inputs;
            this.shaped = shaped;
        }

        @Override
        public void apply() {
            IRecipe recipe = shaped ? new ShapedRecipes(3, 3, inputs, outputStack)
                    : new ShapelessRecipes(outputStack, Arrays.asList(inputs));
            Recipes.registerCustomWoodRecipe(recipe);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void undo() {
            List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
            for (Iterator<IRecipe> iter = recipes.iterator(); iter.hasNext();) {
                IRecipe recipe = iter.next();
                if (recipe.getRecipeOutput() != null && outputStack.isItemEqual(recipe.getRecipeOutput())) {
                    iter.remove();
                }
            }
        }

        @Override
        public String describe() {
            return "Adding CustomWood recipe for " + outputStack.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Undoing adding of CustomWood recipe for " + outputStack.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveRecipeAction implements IUndoableAction {

        private final ItemStack output;
        private final List<IRecipe> removedRecipes = new ArrayList<IRecipe>();

        public RemoveRecipeAction(ItemStack output) {
            this.output = output;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void apply() {
            List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
            for (Iterator<IRecipe> iter = recipes.iterator(); iter.hasNext();) {
                IRecipe recipe = iter.next();
                if (recipe.getRecipeOutput() != null && output.isItemEqual(recipe.getRecipeOutput())) {
                    removedRecipes.add(recipe);
                    iter.remove();
                }
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void undo() {
            CraftingManager.getInstance().getRecipeList().addAll(removedRecipes);
        }

        @Override
        public String describe() {
            return "Removing CustomWood recipe for " + output.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Undoing removal of CustomWood recipe for " + output.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
