package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRecipe;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import net.minecraft.client.multiplayer.ClientLevel;
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
        if(event.getSide().isServer()) {
            AgriCraft.instance.getMinecraftServer().getRecipeManager().getAllRecipesFor(ClocheRecipe.TYPE).stream()
                    .filter(recipe -> recipe instanceof AgriClocheRecipe)
                    .map(recipe -> (AgriClocheRecipe) recipe)
                    .forEach(AgriClocheRecipe::updateOutputs);
        } else {
            ClientLevel world = (ClientLevel) AgriCraft.instance.getClientWorld();
            world.getRecipeManager().getAllRecipesFor(ClocheRecipe.TYPE).stream()
                    .filter(recipe -> recipe instanceof AgriClocheRecipe)
                    .map(recipe -> (AgriClocheRecipe) recipe)
                    .forEach(AgriClocheRecipe::updateOutputs);
        }
    }
}
