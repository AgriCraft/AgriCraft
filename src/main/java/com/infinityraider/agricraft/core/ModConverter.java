/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.util.AgriConverter;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 *
 *
 */
public class ModConverter implements AgriConverter {

    @Override
    public Object toStack(String element, int meta, int amount, String tags, boolean ignoreMeta, boolean useOreDict, List<String> ignoreTags) {
        ItemStack stack = GameRegistry.makeItemStack(element, meta, amount, tags);
        return stack == null ? null : new FuzzyStack(stack, ignoreMeta, useOreDict, ignoreTags);
    }

}
