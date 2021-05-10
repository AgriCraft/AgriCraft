package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.seed.AgriSeedIngredient;
import com.infinityraider.agricraft.content.core.AgriSeedIngredientSerializer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class AgriRecipeSerializerRegistry {
    private static final AgriRecipeSerializerRegistry INSTANCE = new AgriRecipeSerializerRegistry();

    public static AgriRecipeSerializerRegistry getInstance() {
        return INSTANCE;
    }

    public final IIngredientSerializer<AgriSeedIngredient> seed_ingredient;

    private AgriRecipeSerializerRegistry() {
        this.seed_ingredient = new AgriSeedIngredientSerializer();
    }
}
