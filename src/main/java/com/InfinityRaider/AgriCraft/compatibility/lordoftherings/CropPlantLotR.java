package com.InfinityRaider.AgriCraft.compatibility.lordoftherings;

import java.util.ArrayList;
import java.util.Random;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropPlantLotR extends AgriCraftPlantGeneric {

    private final boolean crossed;

    public CropPlantLotR(Item seed, Item fruit, Block plant, boolean crossed) {
        super(new ItemStack(seed), plant, 2, new ItemStack(fruit));
        this.crossed = crossed;
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
        return crossed;
    }

    @Override
    public String getInformation() {
        String name = seed.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index>0?name.substring(index+1):name;
        return "agricraft_journal.lotr_"+name;
    }
}
