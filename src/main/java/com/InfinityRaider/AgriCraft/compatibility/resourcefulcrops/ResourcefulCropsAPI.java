package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirement;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tehnut.resourceful.crops.api.ResourcefulAPI;
import tehnut.resourceful.crops.api.base.Seed;
import tehnut.resourceful.crops.api.base.SeedReq;
import tehnut.resourceful.crops.api.registry.SeedRegistry;

import java.util.ArrayList;

public class ResourcefulCropsAPI extends ResourcefulCropsAPIwrapper {
    protected ResourcefulCropsAPI() {
        super();
    }

    @Override
    protected void init() {
        for(int i=0;i<SeedRegistry.getSize();i++) {
            CropPlant cropPlant = new CropPlantResourcefulCrops(i);
            try {
                CropPlantHandler.registerPlant(cropPlant);
                GrowthRequirementHandler.registerGrowthRequirement(new ItemWithMeta(getSeed(), i), getGrowthRequirement(i));
            } catch (Exception e) {
                LogHelper.printStackTrace(e);
            }
        }

    }

    @Override
    protected Block getPlantBlock() {
        return ResourcefulAPI.crop;
    }

    @Override
    protected Item getSeed() {
        return ResourcefulAPI.seed;
    }

    @Override
    protected int getTier(int meta) {
        return SeedRegistry.getSeed(meta).getTier()+1;
    }

    @Override
    protected ArrayList<ItemStack> getAllFruits(int meta) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        Seed seed = SeedRegistry.getSeed(meta);
        ItemStack fruit1 = seed.getOutputStack();
        if(fruit1!=null && fruit1.getItem()!=null) {
            list.add(fruit1);
        }
        ItemStack fruit2 = seed.getSecondOutputStack();
        if(fruit2!=null && fruit2.getItem()!=null) {
            list.add(fruit1);
        }
        ItemStack fruit3 = seed.getThirdOutputStack();
        if(fruit3!=null && fruit3.getItem()!=null) {
            list.add(fruit1);
        }
        return list;
    }

    protected IGrowthRequirement getGrowthRequirement(int meta) {
        SeedReq seedReq = SeedRegistry.getSeed(meta).getSeedReq();
        GrowthRequirement.Builder builder = new GrowthRequirement.Builder();
        builder.brightnessRange(seedReq.getLightLevelMin(), seedReq.getLightLevelMax());
        builder.requiredBlock(new BlockWithMeta(seedReq.getGrowthReq().getBlock(), seedReq.getGrowthReq().getMeta()), RequirementType.BELOW, false);
        return builder.build();
    }
}
