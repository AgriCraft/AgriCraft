package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IAgriIngredientRenderer<T> extends IIngredientRenderer<T>, IRenderUtilities {}
