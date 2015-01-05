package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

public class CustomCrops {
    public static BlockModPlant[] customCrops;
    public static ItemModSeed[] customSeeds;

    public static void init() {
        initCustomCrops();
        initGrassSeeds();
    }

    private static void initCustomCrops() {
        if(ConfigurationHandler.customCrops) {
            String[] cropsRawData = IOHelper.getLinesArrayFromData(ConfigurationHandler.readCustomCrops());
            customCrops = new BlockModPlant[cropsRawData.length];
            customSeeds = new ItemModSeed[cropsRawData.length];
            for(int i=0;i<cropsRawData.length;i++) {
                String[] cropData = IOHelper.getData(cropsRawData[i]);
                //cropData[0]: name
                //cropData[1]: fruit name:meta
                //cropData[2]: base block name:meta
                //cropData[3]: tier
                //cropData[4]: render type
                //cropData[5]: information
                boolean success = cropData.length==6;
                String errorMsg = "Incorrect amount of arguments";
                LogHelper.debug("parsing "+cropsRawData[i]);
                if(success) {
                    ItemStack fruitStack = IOHelper.getStack(cropData[1]);
                    Item fruit = fruitStack!=null?fruitStack.getItem():null;
                    errorMsg = "Invalid fruit";
                    success = fruit!=null;
                    if(success) {
                        String name = cropData[0];
                        int fruitMeta = fruitStack.getItemDamage(); ItemStack base = IOHelper.getStack(cropData[2]);
                        Block baseBlock = base!=null?((ItemBlock) base.getItem()).field_150939_a:null;
                        int baseMeta = base!=null?base.getItemDamage():0;
                        int tier = Integer.parseInt(cropData[3]);
                        int renderType = Integer.parseInt(cropData[4]);
                        String info = cropData[5];

                        customCrops[i] = new BlockModPlant(baseBlock, baseMeta, fruit, fruitMeta, tier, renderType);
                        RegisterHelper.registerBlock(customCrops[i], Names.Objects.crop + Character.toUpperCase(name.charAt(0)) + name.substring(1));

                        customSeeds[i] = new ItemModSeed(customCrops[i], Character.toUpperCase(name.charAt(0)) + name.substring(1) + " Seeds", info);
                        RegisterHelper.registerItem(customSeeds[i], Names.Objects.seed + Character.toUpperCase(name.charAt(0)) + name.substring(1));

                        OreDictionary.registerOre(Names.OreDict.listAllseed, CustomCrops.customSeeds[i]);
                    }
                }
                if(!success) {
                    LogHelper.info("Error when adding custom crop: "+errorMsg+" (line: "+cropsRawData[i]+")");
                }
            }
            LogHelper.info("Custom crops registered");
        }
    }

    private static void initGrassSeeds() {
        String[] rawData = IOHelper.getLinesArrayFromData(ConfigurationHandler.readGrassDrops());
        for(String data: rawData) {
            String[] dropData = IOHelper.getData(data);
            boolean success = dropData.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+data);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(dropData[0]);
                Item drop = seedStack!=null?seedStack.getItem():null;
                success = drop!=null;
                errorMsg = "Invalid fruit";
                if(success) {
                    int meta = seedStack.getItemDamage();
                    int weight = Integer.parseInt(dropData[1]);
                    MinecraftForge.addGrassSeed(new ItemStack(drop, 1, meta), 10);
                    LogHelper.info("Registered " + Item.itemRegistry.getNameForObject(drop) + ":" + meta + " as a drop from grass (weight: " + weight + ')');
                }
            }
            if(!success) {
                LogHelper.info("Error when adding grass drop: "+errorMsg+" (line: "+data+")");
            }
        }
    }
}