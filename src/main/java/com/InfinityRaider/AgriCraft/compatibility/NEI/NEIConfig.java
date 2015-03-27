package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.item.ItemStack;

public class NEIConfig implements IConfigureNEI {
    private static String version = "1.0";

    @Override
    public void loadConfig() {
        if(ModIntegration.LoadedMods.nei) {
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
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.potato, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.carrot, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.melon, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.pumpkin, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.sugarcane, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.dandelion, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.poppy, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.orchid, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.allium, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.tulipRed, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.tulipOrange, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.tulipWhite, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.tulipPink, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.daisy, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.cactus, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.shroomRed, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.shroomBrown, 1, i));
            //hide botania crops
            if(ConfigurationHandler.integration_Botania) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaWhite, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaOrange, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaMagenta, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaLightBlue, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaYellow, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaLime, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaPink, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaGray, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaLightGray, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaCyan, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaPurple, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaBlue, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaBrown, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaGreen, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaRed, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Crops.botaniaBlack, 1, i));
            }
            //hide resource crops
            if(ConfigurationHandler.resourcePlants) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.diamahlia, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.ferranium, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.aurigold, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.lapender, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.emeryllis, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.redstodendron, 1, i));
                AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.nitorWart, 1, i));
                if(OreDictHelper.oreCopper!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.cuprosia, 1, i));
                }
                if(OreDictHelper.oreTin!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.petinia, 1, i));
                }
                if(OreDictHelper.oreLead!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.plombean, 1, i));
                }
                if(OreDictHelper.oreSilver!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.silverweed, 1, i));
                }
                if(OreDictHelper.oreAluminum!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.jaslumine, 1, i));
                }
                if(OreDictHelper.oreNickel!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.niccissus, 1, i));
                }
                if(OreDictHelper.orePlatinum!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.platiolus, 1, i));
                }
                if(OreDictHelper.oreOsmium!=null) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(ResourceCrops.osmonium, 1, i));
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
