package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.agricraft.content.core.AgriPlantIngredientSerializer;
import com.infinityraider.agricraft.content.core.AnySoilIngredientSerializer;
import com.infinityraider.agricraft.plugins.immersiveengineering.ImmersiveEngineeringPlugin;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public final class AgriRecipeSerializerRegistry extends ModContentRegistry {
    private static final AgriRecipeSerializerRegistry INSTANCE = new AgriRecipeSerializerRegistry();

    public static AgriRecipeSerializerRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<IInfRecipeSerializer<?>> agri_cloche_recipe;
    //public static final IInfRecipeSerializer BOTANY_POTS_CROP_INFO = BotanyPotsPlugin.getAgriCropInfoSerializer();

    public final IIngredientSerializer<AgriPlantIngredient> plant_ingredient;
    public final IIngredientSerializer<AnySoilIngredient> any_soil_ingredient;

    private AgriRecipeSerializerRegistry() {
        super();

        this.agri_cloche_recipe = this.recipe(ImmersiveEngineeringPlugin::getAgriClocheRecipeSerializer);

        this.plant_ingredient = new AgriPlantIngredientSerializer();
        this.any_soil_ingredient =  new AnySoilIngredientSerializer();
    }
}
