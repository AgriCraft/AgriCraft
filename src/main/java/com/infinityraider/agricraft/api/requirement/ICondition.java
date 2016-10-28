/*
 */
package com.infinityraider.agricraft.api.requirement;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Interface representing a part of a growth requirement.
 */
public interface ICondition {

    boolean isMet(IBlockAccess world, BlockPos pos);

    void addDescription(List<String> lines);

    int getComplexity();

}
