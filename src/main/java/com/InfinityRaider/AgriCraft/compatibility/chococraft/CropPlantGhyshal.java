package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
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
import java.util.Random;

public class CropPlantGhyshal extends CropPlant {
    private Item seed;
    private Block plant;

    private Block gysahlGreen;
    private Item gysahlLovely;
    private Item gysahlGold;

    public CropPlantGhyshal() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        super();
        Class chochoCraftBlocks = Class.forName("chococraft.common.config.ChocoCraftBlocks");
        Class chocoCraftItems = Class.forName("chococraft.common.config.ChocoCraftItems");
        seed = (Item) chocoCraftItems.getField("gysahlSeedsItem").get(null);
        plant = (Block) chochoCraftBlocks.getField("gysahlStemBlock").get(null);
        gysahlGreen = (Block) chochoCraftBlocks.getField("gysahlGreenBlock").get(null);
        gysahlLovely = (Item) chocoCraftItems.getField("gysahlLoverlyItem").get(null);
        gysahlGold = (Item) chocoCraftItems.getField("gysahlGoldenItem").get(null);
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(gysahlGreen));
        list.add(new ItemStack(gysahlLovely));
        list.add(new ItemStack(gysahlGold));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        int nr = (int) (Math.ceil((gain + 0.00) / 3));
        while(nr>0) {
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
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return GrowthRequirementHandler.getGrowthRequirement(seed, 0).canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.unit*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        int meta = (int) Math.ceil(( (float) growthStage ) / 2.0F );
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
}
