package com.InfinityRaider.AgriCraft.compatibility.natura;

import com.InfinityRaider.AgriCraft.apiimpl.v1.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.natura.common.NContent;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class CropPlantNatura extends CropPlant {
    private final int seedMeta;

    public CropPlantNatura(int seedMeta) {
        this.seedMeta = seedMeta;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(NContent.seeds, 1 , seedMeta);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(NContent.plantItem, 1, seedMeta*3));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(NContent.plantItem, 1, seedMeta*3);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
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
        return GrowthRequirementHandler.getGrowthRequirement(NContent.seeds, seedMeta).canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.unit*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        //barley: seedMeta = 0
        //cotton: seedMeta = 1
        int meta = 7;
        switch (growthStage) {
            case 0: meta = seedMeta*4;break;
            case 1: meta = seedMeta*4;break;
            case 2: meta = seedMeta*5;break;
            case 3: meta = 1+seedMeta*4;break;
            case 4: meta = 1+seedMeta*5;break;
            case 5: meta = 2+seedMeta*4;break;
            case 6: meta = 2+seedMeta*5;break;
            case 7: meta = 3+seedMeta*5;break;
        }
        return ((ItemSeeds) NContent.seeds).getPlant(null, 0, 0, 0).getIcon(0, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return seedMeta==1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal."+(seedMeta==0?"barleyNatura":"cottonNatura");
    }
}
