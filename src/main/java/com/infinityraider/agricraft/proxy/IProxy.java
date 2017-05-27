package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.apiimpl.PluginHandler;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.core.CoreHandler;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.GrassDropHandler;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.handler.PlayerInteractEventHandler;
import com.infinityraider.agricraft.init.AgriOreDict;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.agricraft.utility.RenderLogger;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface IProxy extends IProxyBase {

    @Override
    default void preInitStart(FMLPreInitializationEvent event) {
        CoreHandler.preInit(event);
        registerEventHandler(AgriCraft.instance);
        StatRegistry.getInstance().registerAdapter(new PlantStats());
        PluginHandler.preInit(event);
    }

    @Override
    default void initStart(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(AgriCraft.instance, new GuiHandler());
        PluginHandler.init();
        initCustomWoodTypes();
    }

    @Override
    default void initEnd(FMLInitializationEvent event) {
        CoreHandler.init();
    }

    @Override
    default void postInitStart(FMLPostInitializationEvent event) {
        PluginHandler.postInit();
        AgriOreDict.upgradeOreDict();
        GrowthRequirementHandler.init();
    }

    default void registerVillagerSkin(int id, String resource) {
    }

    default void initCustomWoodTypes() {
        CustomWoodTypeRegistry.init();
    }

    @Override
    default void registerCapabilities() {
    }

    @Override
    default void registerEventHandlers() {
        registerEventHandler(new PlayerInteractEventHandler());
        registerEventHandler(new GrassDropHandler());
        if (AgriCraftConfig.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    @Override
    default void activateRequiredModules() {
    }

    @Override
    default void initConfiguration(FMLPreInitializationEvent event) {
    }
}
