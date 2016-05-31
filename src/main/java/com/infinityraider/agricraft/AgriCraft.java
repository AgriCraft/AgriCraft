package com.infinityraider.agricraft;

import com.infinityraider.agricraft.apiimpl.APISelector;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.init.*;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.network.NetworkWrapper;
import com.infinityraider.agricraft.proxy.IProxy;
import com.infinityraider.agricraft.reference.Reference;
import com.agricraft.agricore.core.AgriCore;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;

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
 * 	<li> RlonRyan for helping out with the code. </li>
 * 	<li> HenryLoenwind for the API. </li>
 * 	<li> MechWarrior99, SkullyGamingMC, VapourDrive and SkeletonPunk for providing textures. </li>
 * </ul>
 * 
 * I've annotated my code heavily, for myself and for possible others who might learn from it.
 * <br>
 * Oh and keep on modding in the free world!
 * <p>
 * ~ InfinityRaider
 * </p>
 * @author InfinityRaider
 */
@Mod(
		modid = Reference.MOD_ID,
		name = Reference.MOD_NAME,
		version = Reference.MOD_VERSION,
		guiFactory = Reference.GUI_FACTORY_CLASS,
		updateJSON = Reference.UPDATE_URL
)
public class AgriCraft {
    @Mod.Instance(Reference.MOD_ID)
    public static AgriCraft instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static IProxy proxy;
	
    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void preInit(FMLPreInitializationEvent event) {
		// Core
		CoreHandler.preinit(event);
        AgriCore.getLogger("AgriCraft").debug("Starting Pre-Initialization");
        NetworkWrapper.getInstance().initMessages();
        proxy.initConfiguration(event);
        FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
        AgriCraftBlocks.init();
		AgriCraftItems.init();
        AgriCraftCrops.init();
        APISelector.init();
        CompatibilityHandler.getInstance().preInit();
        proxy.registerRenderers();
        AgriCore.getLogger("AgriCraft").debug("Pre-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void init(FMLInitializationEvent event) {
        AgriCore.getLogger("AgriCraft").debug("Starting Initialization");
        proxy.registerEventHandlers();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        AgriCraftEntities.init();
        CompatibilityHandler.getInstance().init();
        AgriCore.getLogger("AgriCraft").debug("Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public static void postInit(FMLPostInitializationEvent event) {
        AgriCore.getLogger("AgriCraft").debug("Starting Post-Initialization");
		// Core
		CoreHandler.postInit(event);
        //Have to do this in postInit because some mods don't register their items/blocks until init
        AgriCraftRecipes.init();
        GrowthRequirementHandler.init();
        CropPlantHandler.init();
        WorldGen.init();
        CompatibilityHandler.getInstance().postInit();
        AgriCore.getLogger("AgriCraft").debug("Post-Initialization Complete");
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		CompatibilityHandler.getInstance().serverStart();
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        ArrayList<String> removedIds = new ArrayList<>();
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
