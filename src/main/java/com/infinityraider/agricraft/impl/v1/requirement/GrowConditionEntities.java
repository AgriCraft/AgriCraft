package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class GrowConditionEntities extends GrowConditionAbstract {
    private final Predicate<Entity> predicate;
    private final BlockPos offset;
    private final double range;
    private final int min;
    private final int max;

    private final int complexity;

    public GrowConditionEntities(int strength, Predicate<Entity> predicate, BlockPos offset, double range, int min, int max) {
        super(strength, RequirementType.ENTITY, offset);
        this.predicate = predicate;
        this.offset = offset;
        this.range = range;
        this.min = min;
        this.max = max;
        this.complexity = (int) (this.range*this.range*this.range);
    }

    @Override
    public int getComplexity() {
        return this.complexity;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        int amount = world.getEntitiesInAABBexcluding(null, this.getBoundingBox(pos), this.predicate).size();
        return amount >= this.min && amount <= this.max;
    }

    private AxisAlignedBB getBoundingBox(BlockPos pos) {
        pos = pos.add(this.offset);
        return new AxisAlignedBB(pos.getX() - (this.range/2), pos.getY() - (this.range/2), pos.getZ() - (this.range/2),
                pos.getX() + (this.range/2), pos.getY() + (this.range/2), pos.getZ() + (this.range/2));
    }
}
