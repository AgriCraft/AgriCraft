package com.InfinityRaider.AgriCraft.api.v1;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IGrowthRequirement {
	//Methods to check if a seed can grow
	//-----------------------------------
	/** @return true, if all the requirements are met */
	public boolean canGrow(World world, int x, int y, int z);

	//public boolean canPlant(World world, int x, int y, int z);

	/** @return true, if the correct base block is present **/
	public boolean isBaseBlockPresent(World world, int x, int y, int z);

	/** @return true, if the light level is between the allowed values */
	//public boolean isBrightnessGood(World world, int x, int y, int z);

	/** @return true, if the given block is a valid soil */
	public boolean isValidSoil(World world, int x, int y, int z);

	/** @return true, if the given block is a valid soil */
	public boolean isValidSoil(BlockWithMeta soil);

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