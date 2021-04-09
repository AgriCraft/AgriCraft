package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.common.extensions.IForgeStructure;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class GrowConditionStructure extends GrowConditionAbstract {
    private final Predicate<IForgeStructure> predicate;
    private final BlockPos offset;

    public GrowConditionStructure(int strength, Predicate<IForgeStructure> predicate, BlockPos offset) {
        super(strength, RequirementType.STRUCTURE, offset, CacheType.FULL);
        this.predicate = predicate;
        this.offset = offset;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return world.getChunk(pos.add(this.offset)).getStructureStarts().values().stream()
                .map(StructureStart::getStructure)
                .anyMatch(this.predicate);
    }
}
