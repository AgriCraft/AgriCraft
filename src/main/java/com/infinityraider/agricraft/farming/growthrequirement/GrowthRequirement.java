package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.requirement.BlockCondition;
import com.infinityraider.agricraft.api.requirement.ICondition;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Encodes all requirements a plant needs to mutate and grow Uses the Builder
 * class inside to construct instances.
 */
public final class GrowthRequirement implements IGrowthRequirement {

    /**
     * Minimum allowed brightness, inclusive *
     */
    private final int minLight;

    /**
     * Maximum allowed brightness, exclusive *
     */
    private final int maxLight;

    private final List<IAgriSoil> soils;
    private final List<ICondition> conditions;

    GrowthRequirement(List<IAgriSoil> soils, List<ICondition> conditions, int minLight, int maxLight) {
        this.soils = soils;
        this.conditions = conditions;
        this.conditions.sort((a, b) -> Integer.compare(a.getComplexity(), b.getComplexity()));
        if (minLight < maxLight) {
            this.minLight = minLight;
            this.maxLight = maxLight;
        } else {
            this.minLight = maxLight;
            this.maxLight = minLight;
        }
    }

    @Override
    public Collection<IAgriSoil> getSoils() {
        return Collections.unmodifiableCollection(this.soils);
    }

    @Override
    public Collection<ICondition> getConditions() {
        return Collections.unmodifiableCollection(this.conditions);
    }

    @Override
    public int getMinLight() {
        return this.minLight;
    }

    @Override
    public int getMaxLight() {
        return this.maxLight;
    }

    @Override
    public boolean hasValidSoil(IBlockAccess world, BlockPos pos) {
        return FuzzyStack.fromBlockState(world.getBlockState(pos.down()))
                .filter(soil -> this.getSoils().stream().anyMatch(e -> e.isVarient(soil)))
                .isPresent();
    }

    @Override
    public boolean hasValidConditions(IBlockAccess world, BlockPos pos) {
        return this.getConditions().stream()
                .allMatch(c -> c.isMet(world, pos));
    }

    @Override
    public boolean hasValidLight(World world, BlockPos pos) {
        // Determine the light level of the block, as per the vanilla method used in BlockCrop.
        final int light = world.getLightFromNeighbors(pos.up());
        // Determine if the light level is in the proper range.
        return light >= this.getMinLight() && light <= this.getMaxLight();
    }

    @Override
    public boolean isMet(World world, BlockPos pos) {
        return this.hasValidSoil(world, pos) && this.hasValidLight(world, pos) && this.hasValidConditions(world, pos);
    }

    @Override
    public Optional<FuzzyStack> getConditionStack() {
        return this.getConditions().stream()
                .filter(c -> c instanceof BlockCondition)
                .map(c -> ((BlockCondition) c).getStack())
                .findFirst();
    }

}
