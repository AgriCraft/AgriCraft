/*
 */
package com.infinityraider.agricraft.api.soil;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriSoilRegistry {

    boolean isSoil(IAgriSoil soil);

    Optional<IAgriSoil> getSoil(String id);

    default Optional<IAgriSoil> getSoil(IBlockState state) {
        return FuzzyStack.fromBlockState(state).flatMap(this::getSoil);
    }

    default Optional<IAgriSoil> getSoil(ItemStack stack) {
        return this.getSoil(new FuzzyStack(stack));
    }

    default Optional<IAgriSoil> getSoil(FuzzyStack stack) {
        return this.getSoils().stream()
                .filter(s -> s.isVarient(stack))
                .findFirst();
    }

    boolean addSoil(IAgriSoil soil);

    boolean removeSoil(IAgriSoil soil);

    Collection<IAgriSoil> getSoils();

    Set<String> getSoilIds();

    default boolean isSoil(FuzzyStack soil) {
        return this.getSoils().stream()
                .anyMatch(s -> s.isVarient(soil));
    }

}
