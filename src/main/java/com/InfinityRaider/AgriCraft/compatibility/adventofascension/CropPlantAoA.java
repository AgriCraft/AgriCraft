package com.InfinityRaider.AgriCraft.compatibility.adventofascension;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.AgriCraftPlantGeneric;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public class CropPlantAoA extends AgriCraftPlantGeneric {
    private final String name;

    public CropPlantAoA(ItemSeeds seeds, int tier, String name) {
        super(seeds, tier);
        this.name = name;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    public String getInformation() {
        return "agricraft_journal.aoa_"+name;
    }
}
