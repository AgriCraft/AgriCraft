package com.InfinityRaider.AgriCraft.compatibility.extrabiomesxl;

import java.util.ArrayList;
import java.util.Random;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CropPlantExtraBiomesXL extends AgriCraftPlantGeneric {

    public CropPlantExtraBiomesXL(ItemStack seed, Block plant, ItemStack...fruit) {
        super(seed, plant, 2, fruit);
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
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.EBXL_strawberry";
    }
}
