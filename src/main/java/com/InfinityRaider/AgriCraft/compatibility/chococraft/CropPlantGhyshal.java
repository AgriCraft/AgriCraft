package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import chococraft.common.config.ChocoCraftBlocks;
import chococraft.common.config.ChocoCraftItems;
import com.InfinityRaider.AgriCraft.api.v1.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantGhyshal extends CropPlant {

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(ChocoCraftItems.gysahlSeedsItem);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(ChocoCraftBlocks.gysahlGreenBlock));
        list.add(new ItemStack(ChocoCraftItems.gysahlLoverlyItem));
        list.add(new ItemStack(ChocoCraftItems.gysahlGoldenItem));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        Block normal = ChocoCraftBlocks.gysahlGreenBlock;
        Item lovely = ChocoCraftItems.gysahlLoverlyItem;
        Item golden = ChocoCraftItems.gysahlGoldenItem;
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        int nr = (int) (Math.ceil((gain + 0.00) / 3));
        while(nr>0) {
            ItemStack fruitStack;
            double random = rand.nextDouble();
            if (gain == 10) {
                Item fruit = random < 0.2 ? golden : (random < 0.6 ? lovely : null);
                if (fruit == null) {
                    fruitStack = new ItemStack(normal);
                } else {
                    fruitStack = new ItemStack(fruit, 1);
                }
            } else {
                fruitStack = (random < gain * 0.04 ? new ItemStack(lovely, 1) : new ItemStack(normal));
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
        return GrowthRequirementHandler.getGrowthRequirement((ItemSeeds) ChocoCraftItems.gysahlSeedsItem, 0).canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        int meta = (int) Math.ceil(( (float) growthStage ) / 2.0F );
        return ChocoCraftBlocks.gysahlStemBlock.getIcon(0, meta);
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
