
package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantDelegate;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantPartialGeneric;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CropPlantGhyshal extends AgriCraftPlantPartialGeneric {

	private ItemStack seed;
	private Block plant;
	private final List<ItemStack> fruits;

	private Block gysahlGreen;
	private Item gysahlLovely;
	private Item gysahlGold;

	public CropPlantGhyshal() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

		Class chochoCraftBlocks = Class.forName("chococraft.common.config.ChocoCraftBlocks");
		Class chocoCraftItems = Class.forName("chococraft.common.config.ChocoCraftItems");

		this.seed = new ItemStack((Item) chocoCraftItems.getField("gysahlSeedsItem").get(null));
		this.plant = (Block) chochoCraftBlocks.getField("gysahlStemBlock").get(null);

		gysahlGreen = (Block) chochoCraftBlocks.getField("gysahlGreenBlock").get(null);
		gysahlLovely = (Item) chocoCraftItems.getField("gysahlLoverlyItem").get(null);
		gysahlGold = (Item) chocoCraftItems.getField("gysahlGoldenItem").get(null);

		this.fruits = new ArrayList<>();
		this.fruits.add(new ItemStack(gysahlGreen));
		this.fruits.add(new ItemStack(gysahlLovely));
		this.fruits.add(new ItemStack(gysahlGold));
	}
	
	@Override
	public List<ItemStack> getAllFruits() {
		return fruits;
	}

	@Override
	public ItemStack getRandomFruit(Random rand) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		int nr = (int) (Math.ceil((gain + 0.00) / 3));
		while (nr > 0) {
			ItemStack fruitStack;
			double random = rand.nextDouble();
			if (gain == 10) {
			Item fruit = random < 0.2 ? gysahlGold : (random < 0.6 ? gysahlLovely : null);
			if (fruit == null) {
				fruitStack = new ItemStack(gysahlGreen);
			} else {
				fruitStack = new ItemStack(fruit, 1);
			}
			} else {
			fruitStack = (random < gain * 0.04 ? new ItemStack(gysahlLovely, 1) : new ItemStack(gysahlGreen));
			}
			list.add(fruitStack);
			nr--;
		}
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getPlantIcon(int growthStage) {
		int meta = (int) Math.ceil((growthStage) / 2.0F);
		return plant.getIcon(0, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsFlower() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getInformation() {
		return "agricraft_journal.ghyshal";
	}

	@Override
	public int getTier() {
		return 2;
	}

	@Override
	public ItemStack getSeed() {
		return seed;
	}

	@Override
	public Block getBlock() {
		return plant;
	}
}
