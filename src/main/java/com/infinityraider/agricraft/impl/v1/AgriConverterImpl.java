package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriConverter;
import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.ImmutableList;
import com.infinityraider.infinitylib.utility.TagUtil;
import com.infinityraider.infinitylib.utility.FuzzyStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AgriConverterImpl implements AgriConverter {
    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> convert(Class<T> token, String element, int amount, boolean useTags, List<String> stateData, String nbt, List<String> ignoreNbt) {
        if (TypeHelper.isType(ItemStack.class, token)) {
            return (Collection<T>) this.fetchItemStacks(element, amount, useTags, nbt, ignoreNbt);
        }
        if (TypeHelper.isType(FuzzyStack.class, token)) {
            return (Collection<T>) this.fetFuzzyStacks(element, amount, useTags, nbt, ignoreNbt);
        }
        if (TypeHelper.isType(BlockState.class, token)) {
            return (Collection<T>) this.fetchBlockStates(element, useTags, stateData);
        }
        if (TypeHelper.isType(FluidState.class, token)) {
            return (Collection<T>) this.fetchFluidStates(element, useTags, stateData);
        }
        return Collections.emptyList();
    }

    protected Collection<ItemStack> fetchItemStacks(String element, int amount, boolean useTags, String data, List<String> ignoreData) {
        return TagUtil.fetchStacks(element, amount, useTags, data, ignoreData);
    }

    protected Collection<FuzzyStack> fetFuzzyStacks(String element, int amount, boolean useTags, String data, List<String> ignoreData) {
        return TagUtil.parseStack(element, amount, useTags, data, ignoreData).map(ImmutableList::of).orElse(ImmutableList.of());
    }

    protected Collection<BlockState> fetchBlockStates(String element, boolean useTags, List<String> stateData) {
        return TagUtil.fetchBlockStates(element, useTags, stateData);
    }

    protected Collection<FluidState> fetchFluidStates(String element, boolean useTags, List<String> stateData) {
        return TagUtil.fetchFluidStates(element, useTags, stateData);
    }
}
