package com.InfinityRaider.AgriCraft.compatibility.rotarycraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;
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

public class CropPlantCanola extends AgriCraftPlantGeneric {
    private final static Item seed = (Item) Item.itemRegistry.getObject("RotaryCraft:rotarycraft_item_canola");
    private final static Block plant = (Block) Block.blockRegistry.getObject("RotaryCraft:rotarycraft_block_canola");

    public CropPlantCanola() {
        super(new ItemStack(seed), plant, 2, new ItemStack(seed, 1, 0));
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(seed, 1, 0);
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
        return GrowthRequirementHandler.getGrowthRequirement(seed, 0).canGrow(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getPlantIcon(int growthStage) {
        int meta;
        if(growthStage==0) {
            meta = 0;
        } else if(growthStage==7) {
            meta = 9;
        } else {
            meta = growthStage+1;
        }
        return plant.getIcon(0, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean renderAsFlower() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getInformation() {
        return "agricraft.journal_RoC.canola";
    }
}
