package com.InfinityRaider.AgriCraft.compatibility.botania;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;

public class BotaniaHelper extends ModHelper {
    public static ArrayList<BlockModPlant> botaniaCrops = new ArrayList<BlockModPlant>();
    public static ArrayList<ItemModSeed> botaniaSeeds = new ArrayList<ItemModSeed>();

    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {
        if(ConfigurationHandler.integration_Botania) {
            for(int i=0;i<16;i++) {
                Object[] args = {Data.botania[i], ModItems.petal, i, null, null, 0, 3, 1};
                String name =(String) args[0];
                //create plant
                BlockModPlant plant = new BlockModPlant(args);
                botaniaCrops.add(plant);
                RegisterHelper.registerCrop(plant, name);
                //create seed
                ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
                botaniaSeeds.add(seed);
                RegisterHelper.registerSeed(seed, plant);
                try {
                    CropPlantHandler.registerPlant(plant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogHelper.info("Botania crops registered");
        }
    }

    @Override
    protected String modId() {
        return "Botania";
    }
}
