package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRenderFunction;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;

public class ImmersiveEngineeringCompat {
    private static final ClocheRenderFunction.ClocheRenderFunctionFactory DEFAULT = (block) -> null;

    public static IInfRecipeSerializer getAgriClocheRecipeSerializer() {
        return AgriClocheRecipe.SERIALIZER;
    }

    public static void registerDummyRenderFunction() {
        ClocheRenderFunction.RENDER_FUNCTION_FACTORIES.put(
                AgriCraft.instance.getModId(), DEFAULT);
    }
}
