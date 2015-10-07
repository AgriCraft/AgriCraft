
package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantStemGeneric;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;

public class CropPlantCreeper extends AgriCraftPlantStemGeneric {

	private static final ItemSeeds loc_soc = (ItemSeeds) Item.itemRegistry.getObject("mobdropcrops:Creeper Seed");
	private static final Block loc_bloc = (Block) Block.blockRegistry.getObject("mobdropcrops:Creeper Pod");

	public CropPlantCreeper() {
		super(new ItemStack(loc_soc), loc_soc.getPlant(null, 0, 0, 0), loc_bloc, 4, new ItemStack(Items.gunpowder));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getPlantIcon(int growthStage) {
		if (growthStage < 7) {
			// for the Vanilla SeedItem class the arguments for this method are not used
			return getBlock().getIcon(0, transformMeta(growthStage));
		} else {
			return getStemIcon();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getStemIcon() {
		BlockStem plant = (BlockStem) getBlock();
		return plant.getStemIcon();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		PlantRenderer.renderStemPlant(x, y, z, renderer, getPlantIcon(meta), meta, getBlock(), getFruitBlock(), meta == Constants.MATURE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getInformation() {
		String name = getSeed().getUnlocalizedName();
		int start = name.indexOf('.') + 1;
		int stop = name.indexOf("seedItem");
		name = name.substring(start, stop);
		return "agricraft_journal.pmd_" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
}
