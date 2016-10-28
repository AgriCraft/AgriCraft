/*
 */
package com.infinityraider.agricraft.core;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.infinityraider.agricraft.api.util.FuzzyStack;

import com.agricraft.agricore.util.AgriConverter;

/**
 *
 *
 */
public class ModConverter implements AgriConverter {

    @Override
    public Object toStack(String element, int meta, int amount, String tags, boolean ignoreMeta, boolean ignoreTags, boolean useOreDict) {
        ItemStack stack = GameRegistry.makeItemStack(element, meta, amount, tags);
        return stack == null ? null : new FuzzyStack(stack, ignoreMeta, ignoreTags, useOreDict);
    }

}
