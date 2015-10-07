
package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generic abstract implementation of the cropPlant, will work for most crops that follow the vanilla item seeds
 */
public abstract class AgriCraftPlantPartialGeneric implements IAgriCraftPlant {

	public int transformMeta(int growthStage) {
		return growthStage;
	}

	@Override
	public ItemStack getRandomFruit(Random rand) {
		List<ItemStack> list = getAllFruits();
		if (list != null && list.size() > 0) {
			return list.get(rand.nextInt(list.size())).copy();
		}
		return null;
	}

	@Override
	public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
		int amount = (int) (Math.ceil((gain + 0.00) / 3));
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		while (amount > 0) {
			list.add(getRandomFruit(rand));
			amount--;
		}
		return list;
	}

	@Override
	public boolean canBonemeal() {
		return getTier() < 4;
	}

	@Override
	public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
		return true;
	}

	@Override
	public boolean isFertile(World world, int x, int y, int z) {
		return GrowthRequirementHandler.getGrowthRequirement(this.getSeed().getItem(), this.getSeed().getItemDamage()).canGrow(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getHeight(int meta) {
		return Constants.UNIT * 13;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getPlantIcon(int growthStage) {
		// for the Vanilla SeedItem class the arguments for this method are not used
		return getBlock().getIcon(0, transformMeta(growthStage));
	}
	
	@Override
	public boolean renderAsFlower() {
		return false;
	}
	
	@Override
	public boolean overrideRendering() {
		return false;
	}
	
	@Override
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		// Do nothing.
	}
	
	@Override
	public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
		// Do nothing.
		return false;
	}
	
	@Override
	public void onPlantRemoved(World world, int x, int y, int z) {
		// Do nothing.
	}
	
	@Override
	public void onSeedPlanted(World world, int x, int y, int z) {
		// Do nothing.
	}
	
	@Override
	public IGrowthRequirement getGrowthRequirement() {
		// TODO: Uhh...
		return null;
	}
	
	@Override
	public String getInformation() {
		return "agricraft_journal."+getSeed().getUnlocalizedName();
	}

}
