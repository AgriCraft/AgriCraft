package com.infinityraider.agricraft.api.requirement;

import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.BlockWithMeta;
import com.infinityraider.agricraft.apiimpl.SoilRegistry;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Preferred method to use this interface is to read or set data to existing
 * GrowthRequirements, use IGrowthRequirementBuilder to create new ones Creating
 * your own implementation is possible, but should only be used in special
 * cases.
 */
public interface IGrowthRequirement {

    //Methods to check if a seed can grow
    //-----------------------------------
    /**
     * @return true, if all the requirements are met (position is the position
     * of the crop)
     */
    boolean canGrow(World world, BlockPos pos);

    //public boolean canPlant(World world, int x, int y, int z);
    /**
     * @return true, if the correct base block is present (position is the
     * position of the crop)
     */
    boolean isBaseBlockPresent(World world, BlockPos pos);

    /**
     * @return true, if the given block is a valid soil (position is the
     * position of the soil)
     */
    boolean isValidSoil(World world, BlockPos pos);

    /**
     * @return true, if the given block is a valid soil
     */
    default boolean isValidSoil(BlockWithMeta soil) {
        final ItemStack stack = soil.toStack();
        return this.isValidSoil(
                SoilRegistry.getInstance().getSoils().stream()
                .filter(s -> s.getVarients().contains(stack))
                .findFirst()
                .orElse(null)
        );
    }

    /**
     * @return true, if the given block is a valid soil
     */
    boolean isValidSoil(IAgriSoil soil);

    //Methods to change specific requirements
    //--------------------------------------
    Collection<IAgriSoil> getSoils();

    int getMinBrightness();

    int getMaxBrightness();

    Optional<BlockWithMeta> getRequiredBlock();

    Optional<RequirementType> getRequiredType();

}
