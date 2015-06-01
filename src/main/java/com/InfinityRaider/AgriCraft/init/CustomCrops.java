package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.sun.javaws.exceptions.InvalidArgumentException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.List;

public class CustomCrops {
    public static BlockModPlant[] customCrops;
    public static ItemModSeed[] customSeeds;

    public static void init() {
        if(ConfigurationHandler.customCrops) {
            String[] cropsRawData = IOHelper.getLinesArrayFromData(ConfigurationHandler.readCustomCrops());
            customCrops = new BlockModPlant[cropsRawData.length];
            customSeeds = new ItemModSeed[cropsRawData.length];
            for(int i=0;i<cropsRawData.length;i++) {
                String[] cropData = IOHelper.getData(cropsRawData[i]);
                //cropData[0]: name
                //cropData[1]: fruit name:meta
                //cropData[2]: soil
                //cropData[3]: base block name:meta
                //cropData[4]: tier
                //cropData[5]: render type
                //cropData[6]: information
                boolean success = cropData.length==7;
                String errorMsg = "Incorrect amount of arguments, arguments should be: (name, fruit:fruitMeta, soil, baseBlock:baseBlockMeta, tier, renderType, information)";
                LogHelper.debug(new StringBuffer("parsing ").append(cropsRawData[i]));
                if(success) {
                    ItemStack fruitStack = IOHelper.getStack(cropData[1]);
                    Item fruit = fruitStack!=null?fruitStack.getItem():null;
                    errorMsg = "Invalid fruit";
                    success = (fruit!=null) || (cropData[1].equals("null")) ;
                    if(success) {
                        String name = cropData[0];
                        int fruitMeta = fruit!=null?fruitStack.getItemDamage():0;
                        Block soil = cropData[2].equalsIgnoreCase("null")?null:((Block) Block.blockRegistry.getObject(cropData[2]));
                        ItemStack base = IOHelper.getStack(cropData[3]);
                        Block baseBlock = base != null ? ((ItemBlock) base.getItem()).field_150939_a : null;
                        int baseMeta = base != null ? base.getItemDamage() : 0;
                        int tier = Integer.parseInt(cropData[4]);
                        RenderMethod renderType = RenderMethod.getRenderMethod(Integer.parseInt(cropData[5]));
                        String info = cropData[6];
                        try {
                            customCrops[i] = new BlockModPlant(new Object[] {name, new ItemStack(fruit, 1, fruitMeta), soil, new BlockWithMeta(baseBlock, baseMeta), tier, renderType});
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                            continue;
                        }
                        customSeeds[i] = customCrops[i].getSeed();
                        customSeeds[i].setInformation(info);
                    }
                }
                if(!success) {
                    LogHelper.info(new StringBuffer("Error when adding custom crop: ").append(errorMsg).append(" (line: ").append(cropsRawData[i]).append(")"));
                }
            }
            LogHelper.info("Custom crops registered");
        }
    }

    public static void initGrassSeeds() {
        if(ConfigurationHandler.wipeTallGrassDrops) {
            List seedList = null;
            boolean error = false;
            try {
                Field fieldSeedList = (ForgeHooks.class).getDeclaredField("seedList");
                fieldSeedList.setAccessible(true);
                seedList = (List) fieldSeedList.get(null);
            } catch (NoSuchFieldException e) {
                error = true;
            } catch (IllegalAccessException e) {
                error = true;
            }
            if(error) {
                LogHelper.info("Error when wiping tall grass drops: couldn't get seed list");
            } else {
                seedList.clear();
                LogHelper.info("Wiped seed entries");
            }
        }
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
                    MinecraftForge.addGrassSeed(new ItemStack(drop, 1, meta), weight);
                    LogHelper.info(new StringBuffer("Registered ").append(Item.itemRegistry.getNameForObject(drop)).append(":").append(meta).append(" as a drop from grass (weight: ").append(weight).append(')'));
                }
            }
            if(!success) {
                LogHelper.info(new StringBuffer("Error when adding grass drop: ").append(errorMsg).append(" (line: ").append(data).append(")"));
            }
        }
    }
}