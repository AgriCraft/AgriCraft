package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirement;
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
            fruit1.stackSize = 1;
            list.add(fruit1);
        }
        ItemStack fruit2 = seed.getSecondOutputStack();
        if(fruit2!=null && fruit2.getItem()!=null) {
            fruit2.stackSize = 1;
            list.add(fruit1);
        }
        ItemStack fruit3 = seed.getThirdOutputStack();
        if(fruit3!=null && fruit3.getItem()!=null) {
            fruit3.stackSize = 1;
            list.add(fruit1);
        }
        return list;
    }

    protected IGrowthRequirement getGrowthRequirement(int meta) {
        SeedReq seedReq = SeedRegistry.getSeed(meta).getSeedReq();
        GrowthRequirement.Builder builder = new GrowthRequirement.Builder();
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
