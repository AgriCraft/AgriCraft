package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriConverter;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.util.TagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AgriConverterImpl implements AgriConverter {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> convert(Class<T> token, String element, int amount, boolean useTags, String data, List<String> ignoreData) {
        if (TypeHelper.isType(ItemStack.class, token)) {
            return (Collection<T>) this.fetchItemStacks(element, amount, useTags, data, ignoreData);
        }
        if (TypeHelper.isType(BlockState.class, token)) {
            return (Collection<T>) this.fetchBlockStates(element, useTags, data, ignoreData);
        }
        if (TypeHelper.isType(FluidState.class, token)) {
            return (Collection<T>) this.fetchFluidStates(element, useTags, data, ignoreData);
        }
        return Collections.emptyList();
    }

    protected Collection<ItemStack> fetchItemStacks(String element, int amount, boolean useTags, String data, List<String> ignoreData) {
        return TagUtil.fetchItemStacks(element, amount, useTags, data, ignoreData);
    }

    protected Collection<BlockState> fetchBlockStates(String element, boolean useTags, String data, List<String> ignoreData) {
        return TagUtil.fetchBlockStates(element, useTags, data, ignoreData);
    }

    protected Collection<FluidState> fetchFluidStates(String element, boolean useTags, String data, List<String> ignoreData) {
        return TagUtil.fetchFluidStates(element, useTags, data, ignoreData);
    }
}
