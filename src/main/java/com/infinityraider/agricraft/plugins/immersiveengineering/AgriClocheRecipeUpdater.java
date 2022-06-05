package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRecipe;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AgriClocheRecipeUpdater {
    private static final AgriClocheRecipeUpdater INSTANCE = new AgriClocheRecipeUpdater();

    public static AgriClocheRecipeUpdater getInstance() {
        return INSTANCE;
    }

    private AgriClocheRecipeUpdater() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlantsInitialized(AgriRegistryEvent.Initialized.Plant event) {
        // fetch recipe manager
        RecipeManager recipeManager = event.getSide().isServer()
                ? AgriCraft.instance.getMinecraftServer().getRecipeManager()
                : AgriCraft.instance.getClientWorld().getRecipeManager();
        // update cloche outputs
        recipeManager.getAllRecipesFor(ClocheRecipe.TYPE).stream()
                    .filter(recipe -> recipe instanceof AgriClocheRecipe)
                    .map(recipe -> (AgriClocheRecipe) recipe)
                    .forEach(AgriClocheRecipe::updateOutputs);
    }
}
