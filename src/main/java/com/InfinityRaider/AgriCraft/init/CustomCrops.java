package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.util.List;

public class CustomCrops {
    public static BlockModPlant[] customCrops;
    public static ItemModSeed[] customSeeds;

    @SuppressWarnings("deprecation")
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
                //cropData[7]: shearable drop (optional)
                boolean success = cropData.length==7 || cropData.length==8;
                String errorMsg = "Incorrect amount of arguments, arguments should be: (name, fruit:fruitMeta, soil, baseBlock:baseBlockMeta, tier, renderType, information, shearable (optional) )";
                LogHelper.debug(new StringBuffer("parsing ").append(cropsRawData[i]));
                if(success) {
                    ItemStack fruitStack = IOHelper.getStack(cropData[1]);
                    errorMsg = "Invalid fruit";
                    success = (fruitStack!=null && fruitStack.getItem()!=null) || (cropData[1].equals("null")) ;
                    if(success) {
                        String name = cropData[0];
                        //soil
                        BlockWithMeta soil =IOHelper.getBlock(cropData[2]);
                        //baseblock
                        BlockWithMeta base = IOHelper.getBlock(cropData[3]);
                        //tier
                        int tier = Integer.parseInt(cropData[4]);
                        //render method
                        RenderMethod renderType = RenderMethod.getRenderMethod(Integer.parseInt(cropData[5]));
                        //shearable
                        ItemStack shearable = cropData.length>7?IOHelper.getStack(cropData[7]):null;
                        shearable = (shearable!=null && shearable.getItem()!=null)?shearable:null;
                        //info
                        String info = cropData[6];
                        try {
                            customCrops[i] = new BlockModPlant(name, fruitStack, soil, RequirementType.BELOW, base, tier, renderType, shearable);
                        } catch (Exception e) {
                            if(ConfigurationHandler.debug) {
                            	LogHelper.printStackTrace(e);
                            }
                            break;
                        }
                        customSeeds[i] = customCrops[i].getSeed();
                        LanguageRegistry.addName(customCrops[i], Character.toUpperCase(name.charAt(0))+name.substring(1));
                        LanguageRegistry.addName(customSeeds[i], Character.toUpperCase(name.charAt(0))+name.substring(1) + " Seeds");
                        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                            customSeeds[i].setInformation(info);
                        }
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