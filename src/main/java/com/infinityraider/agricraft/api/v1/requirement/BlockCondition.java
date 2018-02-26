/*
 */
package com.infinityraider.agricraft.api.v1.requirement;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Simple Condition that requires a block to be within a certain range.
 */
public class BlockCondition implements ICondition {

    @Nonnull
    private final BlockRange range;
    @Nonnull
    private final FuzzyStack stack;
    private final int amount;
    private final int volume;

    public BlockCondition(@Nonnull FuzzyStack stack, @Nonnull BlockRange range) {
        this.stack = Preconditions.checkNotNull(stack);
        this.range = Preconditions.checkNotNull(range);
        this.amount = stack.toStack().getCount();
        this.volume = range.getVolume();
        if (this.amount < 1) {
            throw new IndexOutOfBoundsException("The required amount of blocks must be greater than zero!");
        }
        if (this.amount > this.volume) {
            throw new IndexOutOfBoundsException("Required amount of blocks exceeds volume of range!");
        }
    }

    @Nonnull
    public FuzzyStack getStack() {
        return stack;
    }

    @Nonnull
    public BlockRange getRange() {
        return range;
    }

    @Override
    public boolean isMet(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        // Validate.
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);

        // Return.
        return new BlockRange(this.range, pos).stream()
                .map(world::getBlockState)
                .map(FuzzyStack::from)
                .map(o -> o.orElse(null))
                .filter(this.stack::equals)
                .skip(this.amount - 1)
                .findAny()
                .isPresent();
    }

    @Override
    public void addDescription(@Nonnull Consumer<String> consumer) {
        // Validate
        Preconditions.checkNotNull(consumer);

        // Add description.
        consumer.accept("Required Blocks");
    }

    @Override
    public int getComplexity() {
        return volume;
    }

}
