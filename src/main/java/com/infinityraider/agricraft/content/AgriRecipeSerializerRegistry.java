package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.agricraft.content.core.AgriPlantIngredientSerializer;
import com.infinityraider.agricraft.content.core.AnySoilIngredientSerializer;
import com.infinityraider.agricraft.plugins.botanypots.BotanyPotsPlugin;
import com.infinityraider.agricraft.plugins.immersiveengineering.ImmersiveEngineeringPlugin;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class AgriRecipeSerializerRegistry {
    private static final AgriRecipeSerializerRegistry INSTANCE = new AgriRecipeSerializerRegistry();

    public static AgriRecipeSerializerRegistry getInstance() {
        return INSTANCE;
    }

    public final IInfRecipeSerializer agri_cloche_recipe;
    public final IInfRecipeSerializer botany_pots_crop_info;
    public final IIngredientSerializer<AgriPlantIngredient> plant_ingredient;
    public final IIngredientSerializer<AnySoilIngredient> any_soil_ingredient;

    private AgriRecipeSerializerRegistry() {
        this.agri_cloche_recipe = ImmersiveEngineeringPlugin.getAgriClocheRecipeSerializer();
        this.botany_pots_crop_info = BotanyPotsPlugin.getAgriCropInfoSerializer();
        this.plant_ingredient = new AgriPlantIngredientSerializer();
        this.any_soil_ingredient = new AnySoilIngredientSerializer();
    }
}
