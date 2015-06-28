package com.InfinityRaider.AgriCraft.api.v1;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public interface ICropPlant {
	/** Gets the tier of this plant, can be overridden trough the configs */
	public int tier();

	/** Gets a stack of the seed for this plant */
	public ItemStack getSeed();

	/** Gets an arraylist of all possible fruit drops from this plant */
	public ArrayList<ItemStack> getAllFruits();

	/** Returns a random fruit for this plant */
	public ItemStack getRandomFruit(Random rand);

	/** Returns an ArrayList with amount of random fruit stacks for this plant */
	public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

	/** Gets called right before a harvest attempt, return false to prevent further processing, player may be null if harvested by automation */
	public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player);

	/** This is called right after this plant is planted on a crop, either trough planting, mutation or spreading */
	public void onSeedPlanted(World world, int x, int y, int z);

	/** This is called right after this plant is removed from a crop or a crop holding this plant is broken */
	public void onPlantRemoved(World world, int x, int y, int z);

	/** Allow this plant to be bonemealed or not */
	public boolean canBonemeal();

	/** When a growth thick is allowed for this plant, return true to re-render the crop clientside */
	public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

	/** Checks if the plant can grow on this position */
	public boolean isFertile(World world, int x, int y, int z);

	/** Checks if the plant is mature */
	public boolean isMature(IBlockAccess world, int x, int y, int z);

	/** Gets the height of the crop */
	public float getHeight(int meta);

	/** Gets the icon for the plant, growth stage goes from 0 to 7 (both inclusive, 0 is sprout and 7 is mature) */
	public IIcon getPlantIcon(int growthStage);

	/** Determines how the plant is rendered, return false to render as wheat (#), true to render as a flower (X) */
	public boolean renderAsFlower();

	/** Gets some information about the plant for the journal */
	public String getInformation();

	/** Return true if you want to render the plant yourself, else agricraft will render the plant based on the data returned by the getIcon and renderAsFlower methods */
	@SideOnly(Side.CLIENT)
	public abstract boolean overrideRendering();

	/** This is called when the plant is rendered, this is never called if returned false on overrideRendering */
	@SideOnly(Side.CLIENT)
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer);

}