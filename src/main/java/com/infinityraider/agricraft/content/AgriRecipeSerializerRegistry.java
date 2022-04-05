package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.requirement.AnySoilIngredient;
import com.infinityraider.agricraft.content.core.AgriPlantIngredientSerializer;
import com.infinityraider.agricraft.content.core.AnySoilIngredientSerializer;
import com.infinityraider.agricraft.plugins.botanypots.BotanyPotsPlugin;
import com.infinityraider.agricraft.plugins.immersiveengineering.ImmersiveEngineeringPlugin;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public final class AgriRecipeSerializerRegistry {
    public static final IInfRecipeSerializer AGRI_CLOCHE_RECIPE = ImmersiveEngineeringPlugin.getAgriClocheRecipeSerializer();
    public static final IInfRecipeSerializer BOTANY_POTS_CROP_INFO = BotanyPotsPlugin.getAgriCropInfoSerializer();
    public static final IIngredientSerializer<AgriPlantIngredient> PLANT_INGREDIENT = new AgriPlantIngredientSerializer();
    public static final IIngredientSerializer<AnySoilIngredient> ANY_SOIL_INGREDIENT = new AnySoilIngredientSerializer();
}
