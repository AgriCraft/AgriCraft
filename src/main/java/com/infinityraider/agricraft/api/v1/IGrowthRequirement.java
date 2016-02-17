package com.infinityraider.agricraft.api.v1;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Preferred method to use this interface is to read or set data to existing GrowthRequirements, use IGrowthRequirementBuilder to create new ones
 * Creating your own implementation is possible, but should only be used in special cases.
 */
public interface IGrowthRequirement {
	//Methods to check if a seed can grow
	//-----------------------------------
	/** @return true, if all the requirements are met (position is the position of the crop) */
	boolean canGrow(World world, BlockPos pos);

	//public boolean canPlant(World world, int x, int y, int z);

	/** @return true, if the correct base block is present (position is the position of the crop)
	 */
	boolean isBaseBlockPresent(World world, BlockPos pos);

	/** @return true, if the given block is a valid soil  (position is the position of the soil)*/
	boolean isValidSoil(World world, BlockPos pos);

	/** @return true, if the given block is a valid soil */
	boolean isValidSoil(BlockWithMeta soil);

	/** @return the required block as ItemStack of size 1 */
	ItemStack requiredBlockAsItemStack();

	RequirementType getRequiredType();

	//Methods to change specific requirements
	//--------------------------------------
	BlockWithMeta getSoil();

	void setSoil(BlockWithMeta soil);

	int[] getBrightnessRange();

	void setBrightnessRange(int min, int max);

	void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict);

	BlockWithMeta getRequiredBlock();

	boolean isOreDict();

}