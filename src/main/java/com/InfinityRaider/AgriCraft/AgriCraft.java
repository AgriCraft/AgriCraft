package com.InfinityRaider.AgriCraft;

/*
    This is my first "real" mod, I've made this while learning to use Minecraft Forge to Mod Minecraft. The code might not be optimal but that wasn't the point of this project.

    Cheers to:
        - Pam for trusting me with her source code and support
        - Pahimar for making his code open source and for creating his Let's Mod Reboot Youtube series, I've learned a lot from this (also used some code, credits due where credits due)
        - VSWE for his "Forging a Minecraft Mod" summer courses
        - NealeGaming for his Minecraft modding tutorials on youtube

    I've annotated my code heavily, for myself and for possible others who might learn from it.

    Oh and keep on modding in the free world

        ~ InfinityRaider
*/

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.handler.MutationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.proxy.IProxy;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.reference.SeedInformation;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID,name = Reference.MOD_NAME,version = Reference.VERSION)
public class AgriCraft {
    @Mod.Instance(Reference.MOD_ID)
    public static AgriCraft instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {
        LogHelper.info("Starting Pre-Initialization");
        //find loaded mods
        ModIntegration.LoadedMods.init();
        //register forge event handlers
        proxy.registerEventHandlers();
        //setting up configuration file
        ConfigurationHandler.init(event);
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        //initialize blocks
        Blocks.init();
        //initialize crops
        Crops.init();
        //initialize items
        Items.init();
        LogHelper.info("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        LogHelper.info("Starting Initialization");
        //register resource crops
        ResourceCrops.init();
        //register seeds
        Seeds.init();
        //register gui handler
        NetworkRegistry.INSTANCE.registerGuiHandler(instance , new GuiHandler());
        //initialize tile entities
        proxy.registerTileEntities();
        //initialize renderers
        proxy.registerRenderers();
        //initialize recipes
        Recipes.init();
        //configure mod integration
        ModIntegration.init();
        LogHelper.info("Initialization Complete");
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
        LogHelper.info("Starting Post-Initialization");
        //initialize custom crops
        CustomCrops.init();
        //initialize mutations
        MutationHandler.init();
        //initialize blacklist
        SeedHelper.initSeedBlackList();
        //read spread chance overrides
        SeedHelper.initSpreadChancesOverrides();
        //initialize world gen
        if(!ConfigurationHandler.disableWorldGen) {
            WorldGen.init();
        }
        //configure NEI
        proxy.initNEI();
        //init seed information
        proxy.initSeedInfo();
        LogHelper.info("Post-Initialization Complete");
    }
}
