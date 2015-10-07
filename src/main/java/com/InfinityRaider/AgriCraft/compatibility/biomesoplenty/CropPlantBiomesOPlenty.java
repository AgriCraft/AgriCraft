package com.InfinityRaider.AgriCraft.compatibility.biomesoplenty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class CropPlantBiomesOPlenty extends AgriCraftPlantGeneric {

    public CropPlantBiomesOPlenty(ItemStack seed, Block plant, int meta, ItemStack...fruit) {
        super(seed, plant, meta, fruit);
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        List<ItemStack> list = getAllFruits();
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
    public String getInformation() {
        return "agricraft_journal.BoP_"+this.getBlock().getUnlocalizedName().substring(this.getBlock().getUnlocalizedName().indexOf('.')+1);
    }
}