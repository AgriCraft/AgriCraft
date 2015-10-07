
package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

/**
 * Generic abstract implementation of AgriCraftPlantTall for two-blocks tall plants
 */
public abstract class AgriCraftPlantTallGeneric extends AgriCraftPlantGeneric {
	
	public AgriCraftPlantTallGeneric(ItemSeeds seeds, int tier) {
		super(seeds, tier);
	}
	
	public AgriCraftPlantTallGeneric(ItemStack seed, Block plant, int tier) {
		super(seed, plant, tier);
	}
	
	public AgriCraftPlantTallGeneric(ItemStack seed, Block plant, int tier, ItemStack...fruits) {
		super(seed, plant, tier, fruits);
	}
	
	public AgriCraftPlantTallGeneric(ItemStack seed, Block plant, int tier, List<ItemStack> fruits) {
		super(seed, plant, tier, fruits);
	}

	/** The metadata value for when the bottom block is "fully grown" and the second block starts growing */
	public abstract int maxMetaBottomBlock();

	@SideOnly(Side.CLIENT)
	public boolean renderTopLayer(int growthStage) {
		return growthStage > maxMetaBottomBlock();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getBottomIcon(meta), 0);
		if (renderTopLayer(meta)) {
			PlantRenderer.renderPlantLayer(x, y, z, renderer, renderAsFlower() ? 1 : 6, getPlantIcon(meta), 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getHeight(int meta) {
		return (meta > maxMetaBottomBlock() ? 2 : 1) * Constants.UNIT * 13;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getBottomIcon(int growthStage) {
		if (growthStage < maxMetaBottomBlock()) {
			return getPlantIcon(growthStage);
		}
		return getPlantIcon(maxMetaBottomBlock());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getPlantIcon(int growthStage) {
		// for the Vanilla SeedItem class the arguments for this method are not used
		return getBlock().getIcon(0, transformMeta(growthStage));
	}
}
