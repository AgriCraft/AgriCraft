package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.pam.mobdropcrops.mobdropcrops;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

public class MobDropCropsHelper extends ModHelper {
    @Override
    protected void init() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.blazeseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.boneseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.creeperseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.enderseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.eyeseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.rottenseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.slimeseedItem);
        OreDictionary.registerOre(Names.OreDict.listAllseed, mobdropcrops.tearseedItem);
    }

    @Override
    protected void initPlants() {
        ItemSeeds[] seeds = {
                (ItemSeeds) mobdropcrops.blazeseedItem,
                (ItemSeeds) mobdropcrops.boneseedItem,
                (ItemSeeds) mobdropcrops.enderseedItem,
                (ItemSeeds) mobdropcrops.eyeseedItem,
                (ItemSeeds) mobdropcrops.rottenseedItem,
                (ItemSeeds) mobdropcrops.tearseedItem
        };
        Block[] plants = {
                mobdropcrops.pamblazeCrop,
                mobdropcrops.pamboneCrop,
                mobdropcrops.pamenderCrop,
                mobdropcrops.pameyeCrop,
                mobdropcrops.pamrottenCrop,
                mobdropcrops.pamtearCrop
        };
        //normal crops
        for(int i=0;i<seeds.length;i++) {
            try {
                CropPlant plant = new CropPlantMobDropCrop(seeds[i], plants[i]);
                CropPlantHandler.registerPlant(plant);
                GrowthRequirementHandler.getGrowthRequirement(seeds[i], 0).setSoil(new BlockWithMeta(Blocks.soul_sand));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //creeper crop
        try {
            CropPlantHandler.registerPlant(new CropPlantCreeper());
            GrowthRequirementHandler.getGrowthRequirement(mobdropcrops.creeperseedItem, 0).setSoil(new BlockWithMeta(Blocks.soul_sand));
        } catch(Exception e) {
            e.printStackTrace();
        }
        //slime crop
        try {
            CropPlantHandler.registerPlant(new CropPlantSlime());
            GrowthRequirementHandler.getGrowthRequirement(mobdropcrops.slimeseedItem, 0).setSoil(new BlockWithMeta(Blocks.soul_sand));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return "mobdropcrops";
    }
}
