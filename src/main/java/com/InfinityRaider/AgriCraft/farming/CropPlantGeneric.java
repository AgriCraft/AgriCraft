package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/** Generic abstract implementation of the cropPlant, will work for most crops that follow the vanilla item seeds*/
public abstract class CropPlantGeneric extends CropPlant{
    private final ItemSeeds seed;

    public CropPlantGeneric(ItemSeeds seed) {
        this.seed = seed;
    }

    public abstract int transformMeta(int growthStage);

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
        return(OreDictHelper.getFruitsFromOreDict(getSeed()));
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        ArrayList<ItemStack> list = getAllFruits();
        if(list!=null && list.size()>0) {
            return list.get(rand.nextInt(list.size()));
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
        return GrowthRequirements.getGrowthRequirement(seed, 0).canGrow(world, x, y, z);
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        //for the Vanilla SeedItem class the arguments for this method are not used
        return seed.getPlant(null, 0, 0 ,0).getIcon(0, transformMeta(growthStage));
    }
}
