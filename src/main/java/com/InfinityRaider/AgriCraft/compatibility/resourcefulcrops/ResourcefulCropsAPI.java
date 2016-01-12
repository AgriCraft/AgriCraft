package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.api.v2.IGrowthRequirementBuilder;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tehnut.resourceful.crops.api.ResourcefulAPI;
import tehnut.resourceful.crops.api.base.Seed;
import tehnut.resourceful.crops.api.base.SeedReq;
import tehnut.resourceful.crops.api.registry.SeedRegistry;
import tehnut.resourceful.crops.api.util.BlockStack;

import java.awt.*;
import java.util.ArrayList;

public class ResourcefulCropsAPI extends ResourcefulCropsAPIwrapper {
    protected ResourcefulCropsAPI() {
        super();
    }

    @Override
    protected void init() {
        for(int i=0;i<SeedRegistry.getSize();i++) {
            CropPlant cropPlant = new CropPlantResourcefulCrops(i, getGrowthRequirement(i));
            try {
                CropPlantHandler.registerPlant(cropPlant);
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
        ItemStack fruit = new ItemStack(ResourcefulAPI.shard, 1, meta);
        list.add(fruit);
        return list;
    }

    protected IGrowthRequirement getGrowthRequirement(int meta) {
        SeedReq seedReq = SeedRegistry.getSeed(meta).getSeedReq();
        IGrowthRequirementBuilder builder = GrowthRequirementHandler.getNewBuilder();
        builder.brightnessRange(seedReq.getLightLevelMin(), seedReq.getLightLevelMax());
        BlockStack growthReq = seedReq.getGrowthReq();
        if(growthReq != null) {
            builder.requiredBlock(new BlockWithMeta(growthReq.getBlock(), growthReq.getMeta()), RequirementType.BELOW, false);
        }
        return builder.build();
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected Color getColor(int meta) {
        return SeedRegistry.getSeed(meta).getColor();
    }
}
