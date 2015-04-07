package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.item.ItemStack;

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
            for(BlockModPlant plant : Crops.defaultCrops) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
            }
            //hide botania crops
            if(ConfigurationHandler.integration_Botania) {
                for(BlockModPlant plant : Crops.botaniaCrops) {
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
