package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class AgriCraftNEIHandler extends TemplateRecipeHandler {
    private static HashMap<Class<? extends AgriCraftNEIHandler>, Boolean> handlerStatuses = new HashMap<Class<? extends AgriCraftNEIHandler>, Boolean>();

    public AgriCraftNEIHandler() {
        if(!handlerStatuses.containsKey(this.getClass())) {
            handlerStatuses.put(this.getClass(), true);
        }
    }

    public static void setActive(Class<? extends AgriCraftNEIHandler> clazz, boolean active) {
        handlerStatuses.put(clazz, active);
    }

    public boolean isActive() {
        return handlerStatuses.get(this.getClass());
    }

    @Override
    public final void loadCraftingRecipes(String id, Object... results) {
        if(!isActive()) {
            return;
        }
        loadCraftingRecipesDo(id, results);
    }

    protected abstract void loadCraftingRecipesDo(String id, Object... results);

    @Override
    public final void loadCraftingRecipes(ItemStack result) {
        if (!isActive()) {
            return;
        }
        loadCraftingRecipesDo(result);
    }

    protected abstract void loadCraftingRecipesDo(ItemStack result);

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (!isActive()) {
            return;
        }
        loadUsageRecipesDo(ingredient);
    }

    protected abstract void loadUsageRecipesDo(ItemStack ingredient);
}
