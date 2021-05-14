package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.content.core.AgriSeedIngredientSerializer;
import com.infinityraider.agricraft.plugins.botanypots.BotanyPotsPlugin;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class AgriRecipeSerializerRegistry {
    private static final AgriRecipeSerializerRegistry INSTANCE = new AgriRecipeSerializerRegistry();

    public static AgriRecipeSerializerRegistry getInstance() {
        return INSTANCE;
    }

    public final IInfRecipeSerializer botany_pots_crop_info;
    public final IIngredientSerializer<AgriPlantIngredient> seed_ingredient;

    private AgriRecipeSerializerRegistry() {
        this.botany_pots_crop_info = BotanyPotsPlugin.getAgriCropInfoSerializer();
        this.seed_ingredient = new AgriSeedIngredientSerializer();
    }
}
