
package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import java.util.List;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import scala.actors.threadpool.Arrays;

public class AgriCraftPlantStemGeneric extends AgriCraftPlantGeneric {

	private final Block fruitBlock;

	public AgriCraftPlantStemGeneric(ItemSeeds seeds, Block fruit, int tier) {
		super(seeds, tier);
		this.fruitBlock = fruit;
	}
	
	public AgriCraftPlantStemGeneric(ItemStack seed, Block stem, Block fruit, int tier) {
		super(seed, stem, tier);
		this.fruitBlock = fruit;
	}
	
	public AgriCraftPlantStemGeneric(ItemStack seed, Block stem, Block fruit, int tier, ItemStack...fruits) {
		super(seed, stem, tier, fruits);
		this.fruitBlock = fruit;
	}
	
	public AgriCraftPlantStemGeneric(ItemStack seed, Block stem, Block fruit, int tier, List<ItemStack> fruits) {
		super(seed, stem, tier, fruits);
		this.fruitBlock = fruit;
	}

	public Block getFruitBlock() {
		return fruitBlock;
	}

	@Override
	public int transformMeta(int growthStage) {
		return growthStage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getPlantIcon(int growthStage) {
		if (growthStage < 7) {
			// for the Vanilla SeedItem class the arguments for this method are not used
			return super.getPlantIcon(growthStage);
		} else {
			return getStemIcon();
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getStemIcon() {
		BlockStem plant = (BlockStem) ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
		return plant.getStemIcon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsFlower() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean mature = (meta == Constants.MATURE);
		Block vine = ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
		PlantRenderer.renderStemPlant(x, y, z, renderer, getPlantIcon(meta), meta, vine, fruitBlock, mature);
	}

	@Override
	public String getInformation() {
		String name = getSeed().getUnlocalizedName();
		if (name.indexOf('_') >= 0) {
			name = name.substring(name.indexOf('_') + 1);
		}
		if (name.indexOf('.') >= 0) {
			name = name.substring(name.indexOf('.') + 1);
		}
		return "agricraft_journal." + name;
	}
}
