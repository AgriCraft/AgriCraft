package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockCustomWood;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class NEIConfig implements IConfigureNEI {
    private static String version = "1.0";

    @Override
    public void loadConfig() {
        if(LoadedMods.nei) {
            //register NEI recipe handler
            if(ConfigurationHandler.enableNEI) {
                LogHelper.debug("Registering NEI recipe handler");
                API.registerRecipeHandler(new NEICropMutationHandler());
                API.registerUsageHandler(new NEICropMutationHandler());
            }
            //hide crop blocks in NEI
            hideItems();
        }
    }

    private static void hideItems() {
        LogHelper.debug("Hiding crops in NEI");
        for (int i = 0; i < 16; i++) {
            //hide crops block
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Blocks.blockCrop, 1, i));
            //hide sprinkler
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Blocks.blockSprinkler, 1, i));
            //hide plant blocks
            for(BlockModPlant plant : Crops.crops) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
            }
            //hide botania crops
            if(ConfigurationHandler.integration_Botania) {
                for(BlockModPlant plant : BotaniaHelper.botaniaCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide resource crops
            if(ConfigurationHandler.resourcePlants) {
                for(BlockModPlant plant : ResourceCrops.vanillaCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
                for(BlockModPlant plant : ResourceCrops.modCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide custom crops
            if(ConfigurationHandler.customCrops) {
                for (BlockModPlant customCrop:CustomCrops.customCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(customCrop, 1, i));
                }
            }
            //hide debugger
            if (ConfigurationHandler.debug) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Items.debugItem, 1, i));
            }
        }
        LogHelper.debug("Hiding custom wood objects");
        Field[] blocks = Blocks.class.getDeclaredFields();
        for(Field field:blocks) {
            if(BlockCustomWood.class.isAssignableFrom(field.getType())) {
                try {
                    Block block = (Block) field.get(null);
                    if(block==null) {
                        continue;
                    }
                    ItemStack stack = new ItemStack(block);
                    ArrayList<ItemStack> list = new ArrayList<ItemStack>();
                    list.add(stack);
                    API.setItemListEntries(stack.getItem(), list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String getName() {
        return Reference.MOD_ID+"_NEI";
    }

    @Override
    public String getVersion() {
        return version;
    }

}
