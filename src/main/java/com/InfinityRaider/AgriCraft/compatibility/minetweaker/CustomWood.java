package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.utility.LogHelper;
import minetweaker.IUndoableAction;
import minetweaker.api.item.IIngredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.agricraft.CustomWood")
public class CustomWood {

    @ZenMethod
    public static void addRecipe(IIngredient[] inputs, IIngredient outputStack, boolean shaped) {
        LogHelper.debug("Add recipe for CustomWood called.");
    }

    @ZenMethod
    public static void removeRecipe(IIngredient output) {
        LogHelper.debug("Remove recipe for CustomWood called.");
    }

    private static class AddRecipeAction implements IUndoableAction {

        @Override
        public void apply() {

        }

        @Override
        public boolean canUndo() {
            return false;
        }

        @Override
        public void undo() {

        }

        @Override
        public String describe() {
            return null;
        }

        @Override
        public String describeUndo() {
            return null;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveRecipeAction implements IUndoableAction {

        @Override
        public void apply() {

        }

        @Override
        public boolean canUndo() {
            return false;
        }

        @Override
        public void undo() {

        }

        @Override
        public String describe() {
            return null;
        }

        @Override
        public String describeUndo() {
            return null;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
