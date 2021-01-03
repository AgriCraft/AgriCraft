package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class GrowConditionBlockStates extends GrowConditionAbstract {
    private final int min;
    private final int max;
    private final Predicate<BlockState> predicate;

    public GrowConditionBlockStates(int strength, RequirementType type, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate) {
        super(strength, type, getOffsets(minOffset, maxOffset));
        this.min = min;
        this.max = max;
        this.predicate = predicate;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        long count = this.offsetsToCheck().stream()
                .map(offset -> world.getBlockState(pos.add(offset)))
                .filter(this.predicate)
                .count();
        return count >= this.min && count <= this.max;
    }

    private static ImmutableSet<BlockPos> getOffsets(BlockPos min, BlockPos max) {
        ImmutableSet.Builder<BlockPos> builder = new ImmutableSet.Builder<>();
        for(int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    builder.add(new BlockPos(x, y, z));
                }
            }
        }
        return builder.build();
    }
}
