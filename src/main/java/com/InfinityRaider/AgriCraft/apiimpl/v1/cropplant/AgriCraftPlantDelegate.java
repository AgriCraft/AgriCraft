
package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import java.util.List;
import java.util.Random;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * The main class used by TileEntityCrop. This is the internal wrapper (implementation) for interfaced classes.
 */
public class AgriCraftPlantDelegate {

	private final IAgriCraftPlant delegatee;

	public AgriCraftPlantDelegate(IAgriCraftPlant plant) {
		this.delegatee = plant;
	}
	
	public IAgriCraftPlant getDelegatee() {
		return delegatee;
	}

	public final int getGrowthRate() {
		int tier = getTier();

		if (tier > 0 && tier <= Constants.GROWTH_TIER.length) {
			return Constants.GROWTH_TIER[tier];
		} else {
			return Constants.GROWTH_TIER[0];
		}
	}

	public final int getTier() {
		int seedTierOverride = SeedHelper.getSeedTierOverride(delegatee.getSeed());
		if (seedTierOverride > 0) {
			return seedTierOverride;
		}
		return this.delegatee.getTier();
	}

	public boolean isMature(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) >= Constants.MATURE;
	}

	@SideOnly(Side.CLIENT)
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		if (delegatee.overrideRendering()) {
			delegatee.renderPlantInCrop(world, x, y, z, renderer);
		} else {
			PlantRenderer.renderPlantLayer(x, y, z, renderer, delegatee.renderAsFlower() ? 1 : 6, delegatee.getPlantIcon(world.getBlockMetadata(x, y, z)), 0);
		}
	}

	// Auto generated delegatee methods.
	
	public IGrowthRequirement getGrowthRequirement() {
		return delegatee.getGrowthRequirement();
	}

	public ItemStack getSeed() {
		return delegatee.getSeed();
	}

	public Block getBlock() {
		return delegatee.getBlock();
	}

	public List<ItemStack> getAllFruits() {
		return delegatee.getAllFruits();
	}

	public ItemStack getRandomFruit(Random rand) {
		return delegatee.getRandomFruit(rand);
	}

	public List<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
		return delegatee.getFruitsOnHarvest(gain, rand);
	}

	public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
		return delegatee.onHarvest(world, x, y, z, player);
	}

	public void onSeedPlanted(World world, int x, int y, int z) {
		delegatee.onSeedPlanted(world, x, y, z);
	}

	public void onPlantRemoved(World world, int x, int y, int z) {
		delegatee.onPlantRemoved(world, x, y, z);
	}

	public boolean canBonemeal() {
		return delegatee.canBonemeal();
	}

	public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
		return delegatee.onAllowedGrowthTick(world, x, y, z, oldGrowthStage);
	}

	public boolean isFertile(World world, int x, int y, int z) {
		return delegatee.isFertile(world, x, y, z);
	}

	public float getHeight(int meta) {
		return delegatee.getHeight(meta);
	}

	public IIcon getPlantIcon(int growthStage) {
		return delegatee.getPlantIcon(growthStage);
	}

	public boolean renderAsFlower() {
		return delegatee.renderAsFlower();
	}

	public String getInformation() {
		return delegatee.getInformation();
	}

	public boolean overrideRendering() {
		return delegatee.overrideRendering();
	}

}
