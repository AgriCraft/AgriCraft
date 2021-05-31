package com.infinityraider.agricraft.plugins.immersiveengineering;

import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;

public class ImmersiveEngineeringCompat {
    public static IInfRecipeSerializer getAgriClocheRecipeSerializer() {
        return AgriClocheRecipe.SERIALIZER;
    }
}
