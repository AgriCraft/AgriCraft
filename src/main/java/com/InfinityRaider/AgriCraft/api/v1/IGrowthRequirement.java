package com.InfinityRaider.AgriCraft.api.v1;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IGrowthRequirement {

	public boolean needsCrops();

	public List<BlockWithMeta> getSoilBlocks();

	public List<BlockWithMeta> getBelowBlocks();

	public List<BlockWithMeta> getNearBlocks();

	public boolean needsTilling();

	//Methods to check if a seed can grow
	//-----------------------------------
	/** @return true, if all the requirements are met */
	public boolean canGrow(World world, int x, int y, int z);

	public boolean canPlant(World world, int x, int y, int z);

	/** @return true, if the correct base block is present **/
	public boolean isBaseBlockPresent(World world, int x, int y, int z);

	/** @return true, if the light level is between the allowed values */
	public boolean isBrightnessGood(World world, int x, int y, int z);

	/** @return true, if the given block is a valid soil */
	public boolean isValidSoil(World world, int x, int y, int z);

	public boolean isValidSoil(BlockWithMeta soil);

	/** @return true, if the given block requires a specific soil */
	public boolean requiresSpecificSoil();

	/** @return true, if the plant requires a base block beneath or nearby it */
	public boolean requiresBaseBlock();

	/** @return the required block as ItemStack of size 1 */
	public ItemStack requiredBlockAsItemStack();

	public RequirementType getRequiredType();

	//Methods to change specific requirements
	//--------------------------------------
	public BlockWithMeta getSoil();

	public void setSoil(BlockWithMeta soil);

	public int[] getBrightnessRange();

	public void setBrightnessRange(int min, int max);

	public void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict);

	public BlockWithMeta getRequiredBlock();

	public boolean isOreDict();

}