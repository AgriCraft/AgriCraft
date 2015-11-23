package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirement;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ResourcefulCropsAPIwrapper {
    private static ResourcefulCropsAPIwrapper instance;

    public static ResourcefulCropsAPIwrapper getInstance() {
        if(instance == null) {
            if(ModHelper.allowIntegration(Names.Mods.resourcefulCrops)) {
                instance = new ResourcefulCropsAPI();
            } else {
                instance = new ResourcefulCropsAPIwrapper();
            }
        }
        return instance;
    }

    protected ResourcefulCropsAPIwrapper() {}

    protected void init() {}

    protected Block getPlantBlock() {
        return null;
    }

    protected Item getSeed() {
        return null;
    }

    protected int getTier(int meta) {
        return 0;
    }

    protected ArrayList<ItemStack> getAllFruits(int meta) {
        return new ArrayList<ItemStack>();
    }

    protected IGrowthRequirement getGrowthRequirement(int meta) {
        return new GrowthRequirement.Builder().build();
    }
}
