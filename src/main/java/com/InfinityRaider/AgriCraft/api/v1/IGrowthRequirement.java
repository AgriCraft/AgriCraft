package com.InfinityRaider.AgriCraft.api.v1;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Preferred method to use this interface is to read or set data to existing GrowthRequirements, use IGrowthRequirementBuilder to create new ones
 * Creating your own implementation is possible, but should only be used in special cases.
 */
public interface IGrowthRequirement {
	//Methods to check if a seed can grow
	//-----------------------------------
	/** @return true, if all the requirements are met */
	boolean canGrow(World world, int x, int y, int z);

	//public boolean canPlant(World world, int x, int y, int z);

	/** @return true, if the correct base block is present **/
	boolean isBaseBlockPresent(World world, int x, int y, int z);

	/** @return true, if the given block is a valid soil */
	boolean isValidSoil(World world, int x, int y, int z);

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