package com.infinityraider.agricraft.proxy;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.apiimpl.PluginHandler;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.handler.PlayerInteractEventHandler;
import com.infinityraider.agricraft.init.AgriEntities;
import com.infinityraider.agricraft.init.AgriRecipes;
import com.infinityraider.agricraft.init.WorldGen;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.RenderLogger;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;

public interface IProxy extends IProxyBase {
    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        CoreHandler.preinit(event);
        registerEventHandler(AgriCraft.instance);
        StatRegistry.getInstance().registerAdapter(new PlantStats());
        PluginHandler.preInit(event);
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(AgriCraft.instance, new GuiHandler());
        AgriEntities.init();
        PluginHandler.init();
        initCustomWoodTypes();
    }

    @Override
    default void postInitStart(FMLPostInitializationEvent event) {
        CoreHandler.postInit(event);
        PluginHandler.postInit();
        AgriRecipes.init();
        GrowthRequirementHandler.init();
        WorldGen.init();
    }

    default void registerVillagerSkin(int id, String resource) {}

    default void initCustomWoodTypes() {
        CustomWoodType.init();
    }

    @Override
    default void registerEventHandlers() {
        registerEventHandler(new PlayerInteractEventHandler());
        if (AgriCraftConfig.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    @Override
    default void activateRequiredModules() {}

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {}
}
