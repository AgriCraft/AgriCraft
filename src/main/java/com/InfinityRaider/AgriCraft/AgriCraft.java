package com.InfinityRaider.AgriCraft;

import java.util.ArrayList;

import com.InfinityRaider.AgriCraft.apiimpl.APISelector;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.init.CropProducts;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.init.CustomCrops;
import com.InfinityRaider.AgriCraft.init.Entities;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.init.Recipes;
import com.InfinityRaider.AgriCraft.init.ResourceCrops;
import com.InfinityRaider.AgriCraft.init.WorldGen;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.proxy.IProxy;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RenderLogger;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * <p>
 * This is my first "real" mod, I've made this while learning to use Minecraft Forge to Mod Minecraft.
 * The code might not be optimal but that wasn't the point of this project.
 * </p>
 * Cheers to:
 * <ul>
 * 	<li> Pam for trusting me with her source code and support. </li>
 * 	<li> Pahimar for making his code open source and for creating his Let's Mod Reboot Youtube series,
 * 		 I've learned a lot from this (also used some code, credit's due where credit's due). </li>
 * 	<li> VSWE for his "Forging a Minecraft Mod" summer courses. </li>
 * 	<li> NealeGaming for his Minecraft modding tutorials on youtube. </li>
 * 	<li> Imasius (a.k.a. Nimo) for learning me to better code in java. </li>
 * 	<li> HenryLoenwind for the API
 * </ul>
 * 
 * I've annotated my code heavily, for myself and for possible others who might learn from it.
 * <br>
 * Oh and keep on modding in the free world
 * <p>
 * ~ InfinityRaider
 * </p>
 * @author InfinityRaider
 */
@Mod(modid = Reference.MOD_ID,name = Reference.MOD_NAME,version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class AgriCraft {
    @Mod.Instance(Reference.MOD_ID)
    public static AgriCraft instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LogHelper.debug("Starting Pre-Initialization");
        NetworkWrapperAgriCraft.init();
        proxy.initConfiguration(event);
        ModHelper.findHelpers();
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        if (ConfigurationHandler.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
        Blocks.init();
        Crops.init();
        Items.init();
        APISelector.init();
        LogHelper.debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        LogHelper.debug("Starting Initialization");
        proxy.registerEventHandlers();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        proxy.registerRenderers();
        Entities.init();
        ModHelper.initHelpers();
        LogHelper.debug("Initialization Complete");
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        LogHelper.debug("Starting Post-Initialization");
        //Have to do this in postInit because some mods don't register their items/blocks until init
        ResourceCrops.init();
        CustomCrops.init();
        Recipes.init();
        SeedHelper.init();
        GrowthRequirementHandler.init();
        CropPlantHandler.init();
        CropProducts.init();
        WorldGen.init();
        CustomCrops.initGrassSeeds();
        ModHelper.postInit();
        LogHelper.debug("Post-Initialization Complete");
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        MutationHandler.init();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        ArrayList<String> removedIds = new ArrayList<String>();
        removedIds.add("AgriCraft:cropMelon");
        removedIds.add("AgriCraft:cropPumpkin");
        removedIds.add("AgriCraft:sprinklerItem");
        for(FMLMissingMappingsEvent.MissingMapping missingMapping: event.get()) {
            if(removedIds.contains(missingMapping.name)) {
                missingMapping.ignore();
            }
        }
    }
}
