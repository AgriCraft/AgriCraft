package com.InfinityRaider.AgriCraft.compatibility.botania;

import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.sun.javaws.exceptions.InvalidArgumentException;
import net.minecraft.item.ItemStack;
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
                Object[] args = {Data.botania[i], new ItemStack(ModItems.petal, 1, i), 3, RenderMethod.CROSSED};
                BlockModPlant plant;
                try {
                    plant = new BlockModPlant(args);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                    continue;
                }
                botaniaCrops.add(plant);
                botaniaSeeds.add(plant.getSeed());
            }
            LogHelper.info("Botania crops registered");
        }
    }

    @Override
    protected String modId() {
        return "Botania";
    }
}
