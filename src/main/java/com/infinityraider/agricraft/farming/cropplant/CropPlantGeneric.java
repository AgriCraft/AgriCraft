package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generic abstract implementation of the cropPlant, will work for most crops that follow the vanilla item seeds
 */
public abstract class CropPlantGeneric extends CropPlant {
	
    private final ItemSeeds seed;
    private final Block plant;
    private ArrayList<ItemStack> fruits;

    public CropPlantGeneric(ItemSeeds seed) {
        this.seed = seed;
        this.plant = seed.getPlant(null, null).getBlock();
        this.fruits = OreDictHelper.getFruitsFromOreDict(getSeed(), modSpecificFruits());
		AgriCraftItems.clipping.addPlant(this, this.plant.getRegistryName().replaceFirst(":", ":blocks/") + 4);
    }
	
	protected CropPlantGeneric(ItemSeeds seed, String texture) {
		this.seed = seed;
        this.plant = seed.getPlant(null, null).getBlock();
        this.fruits = OreDictHelper.getFruitsFromOreDict(getSeed(), modSpecificFruits());
		AgriCraftItems.clipping.addPlant(this, texture);
	}

    protected boolean modSpecificFruits() {
        return AgriCraftConfig.modSpecificDrops;
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
    public Block getBlock() {
        return plant;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
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
        ArrayList<ItemStack> list = new ArrayList<>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

	@Override
    public boolean canBonemeal() {
        return getTier()<4;
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().build();
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }
}
