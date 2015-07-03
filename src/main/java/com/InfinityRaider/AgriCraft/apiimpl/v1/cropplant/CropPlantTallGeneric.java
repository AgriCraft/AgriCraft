package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generic abstract implementation of CropPlantTall for two-blocks tall plants
 */
public abstract class CropPlantTallGeneric extends CropPlantTall {
    private final ItemSeeds seed;

    public CropPlantTallGeneric(ItemSeeds seed) {
        this.seed = seed;
    }

    public abstract int transformMeta(int growthStage);

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
        return OreDictHelper.getFruitsFromOreDict(getSeed());
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

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return GrowthRequirementHandler.getGrowthRequirement(seed, 0).canGrow(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return (meta>maxMetaBottomBlock()?2:1)*Constants.unit*13;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBottomIcon(int growthStage) {
        if(growthStage<maxMetaBottomBlock()) {
            return getPlantIcon(growthStage);
        }
        return getPlantIcon(maxMetaBottomBlock());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        //for the Vanilla SeedItem class the arguments for this method are not used
        return seed.getPlant(null, 0, 0 ,0).getIcon(0, transformMeta(growthStage));
    }
}
