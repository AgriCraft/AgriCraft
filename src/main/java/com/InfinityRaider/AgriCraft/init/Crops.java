package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.*;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;

public class Crops {
    //Vanilla crops
    public static ArrayList<BlockCrops> vanillaCrops;
    public static ArrayList<ItemSeeds> vanillaSeeds;
    //AgriCraft crops
    public static ArrayList<BlockModPlant> defaultCrops;
    public static ArrayList<ItemModSeed> defaultSeeds;

    //Botania crops
    public static ArrayList<BlockModPlant> botaniaCrops;
    public static ArrayList<ItemModSeed> botaniaSeeds;

    public static void initVanillaCrops() {
        vanillaCrops = new ArrayList<BlockCrops>();
        vanillaSeeds = new ArrayList<ItemSeeds>();
        //wheat
        vanillaCrops.add((BlockCrops) net.minecraft.init.Blocks.wheat);
        vanillaSeeds.add((ItemSeeds) net.minecraft.init.Items.wheat_seeds);
        //melon
        Object[] melonArgs = {"Melon", net.minecraft.init.Items.melon, 0, null, null, 0, 1, 6};
        BlockModPlant melon = new BlockModPlant(melonArgs);
        RegisterHelper.registerCrop(melon, (String) melonArgs[0]);
        vanillaCrops.add(melon);
        vanillaSeeds.add((ItemSeeds) Items.melon_seeds);
        //pumpkin
        Object[] pumpkinArgs = {"Pumpkin", Item.getItemFromBlock(net.minecraft.init.Blocks.pumpkin), 0, null, null, 0, 1, 6};
        BlockModPlant pumpkin = new BlockModPlant(pumpkinArgs);
        RegisterHelper.registerCrop(pumpkin, (String) pumpkinArgs[0]);
        vanillaCrops.add(pumpkin);
        vanillaSeeds.add((ItemSeeds) Items.pumpkin_seeds);
    }

    public static void initDefaults() {
        defaultCrops = new ArrayList<BlockModPlant>();
        defaultSeeds = new ArrayList<ItemModSeed>();
        for(Object[] data: Data.defaults) {
            String name =(String) data[0];
            //create plant
            BlockModPlant plant = new BlockModPlant(data);
            defaultCrops.add(plant);
            RegisterHelper.registerCrop(plant, name);
            //create seed
            ItemModSeed seed = new ItemModSeed(plant, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
            defaultSeeds.add(seed);
            RegisterHelper.registerSeed(seed, plant);
        }
        LogHelper.info("Crops registered");
    }

    public static void initBotaniaCrops() {
        if(LoadedMods.botania && ConfigurationHandler.integration_Botania) {
            botaniaCrops = new ArrayList<BlockModPlant>();
            botaniaSeeds = new ArrayList<ItemModSeed>();
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
            }
            LogHelper.info("Botania crops registered");
        }
    }
}