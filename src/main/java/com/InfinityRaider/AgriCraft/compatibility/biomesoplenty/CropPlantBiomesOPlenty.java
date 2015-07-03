package com.InfinityRaider.AgriCraft.compatibility.biomesoplenty;

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

public class CropPlantBiomesOPlenty extends CropPlant {
    private final Item seed;
    private final ItemStack fruit;
    private final Block plant;

    public CropPlantBiomesOPlenty(Item seed, Block plant, ItemStack fruit) {
        this.seed = seed;
        this.plant = plant;
        this.fruit = fruit;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed);
    }


    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(this.fruit);
        return fruits;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        ArrayList<ItemStack> list = getAllFruits();
        if(list!=null && list.size()>0) {
            return list.get(rand.nextInt(list.size())).copy();
        }
        return null;
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
        return this.plant.getIcon(0, growthStage);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.BoP_"+plant.getUnlocalizedName().substring(plant.getUnlocalizedName().indexOf('.')+1);
    }
}