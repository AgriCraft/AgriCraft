/*
 */
package com.infinityraider.agricraft.api.requirement;

import com.infinityraider.agricraft.api.util.BlockRange;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Simple Condition that requires a block to be within a certain range.
 */
public class BlockCondition implements ICondition {

    private final BlockRange range;
    private final FuzzyStack stack;
    private final int amount;
    private final int volume;

    public BlockCondition(FuzzyStack stack, BlockRange range) {
        this.amount = stack.toStack().stackSize;
        this.volume = range.getVolume();
        if (this.amount < 1) {
            throw new IndexOutOfBoundsException("The required amount of blocks must be greater than zero!");
        }
        if (this.amount > this.volume) {
            throw new IndexOutOfBoundsException("Required amount of blocks exceeds volume of range!");
        }
        this.range = range;
        this.stack = stack;
    }

    public FuzzyStack getStack() {
        return stack;
    }

    public BlockRange getRange() {
        return range;
    }

    @Override
    public boolean isMet(IBlockAccess world, BlockPos pos) {
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
    public void addDescription(List<String> lines) {
        lines.add("Required Blocks");
    }

    @Override
    public int getComplexity() {
        return volume;
    }

}
