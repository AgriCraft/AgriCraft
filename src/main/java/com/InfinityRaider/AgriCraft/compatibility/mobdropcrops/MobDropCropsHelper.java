package com.InfinityRaider.AgriCraft.compatibility.mobdropcrops;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MobDropCropsHelper extends ModHelper {
    @Override
    protected void initPlants() {
        //creeper crop
        try {
            ItemSeeds seed = (ItemSeeds) Item.itemRegistry.getObject("mobdropcrops:Creeper Seed");
            if(checkAndPrintWarningMessage(seed)) {
                CropPlant plant = new CropPlantCreeper();
                CropPlantHandler.registerPlant(plant);
                OreDictionary.registerOre(Names.OreDict.listAllseed, plant.getSeed());
                plant.getGrowthRequirement().setSoil(new BlockWithMeta(Blocks.soul_sand));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //slime crop
        try {
            ItemSeeds seed = (ItemSeeds) Item.itemRegistry.getObject("mobdropcrops:slimeseedItem");
            if(checkAndPrintWarningMessage(seed)) {
                CropPlant plant = new CropPlantSlime();
                CropPlantHandler.registerPlant(plant);
                OreDictionary.registerOre(Names.OreDict.listAllseed, plant.getSeed());
                plant.getGrowthRequirement().setSoil(new BlockWithMeta(Blocks.soul_sand));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //normal crops
        Class registry;
        try {
            registry = Class.forName("com.pam.mobdropcrops.mobdropcrops");
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
            return;
        }
        for(Field field: registry.getDeclaredFields()) {
            try {
                if(Modifier.isStatic(field.getModifiers())) {
                    Object obj = field.get(null);
                    if(!(obj instanceof ItemSeeds)) {
                        continue;
                    }
                    ItemSeeds seed = (ItemSeeds) obj;
                    if(checkAndPrintWarningMessage(seed)) {
                        CropPlant plant = new CropPlantMobDropCrop(seed);
                        CropPlantHandler.registerPlant(plant);
                        OreDictionary.registerOre(Names.OreDict.listAllseed, plant.getSeed());
                        plant.getGrowthRequirement().setSoil(new BlockWithMeta(Blocks.soul_sand));
                    }
                }
            } catch(Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    private boolean checkAndPrintWarningMessage(ItemSeeds seed) {
        if(seed.getPlant(null, 0, 0, 0) == null) {
            LogHelper.info("NOT REGISTERING CROPPLANT FOR " + Item.itemRegistry.getNameForObject(seed) + " BECAUSE INVALID IMPLEMENTATION OF REQUIRED METHODS");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected String modId() {
        return "mobdropcrops";
    }
}
