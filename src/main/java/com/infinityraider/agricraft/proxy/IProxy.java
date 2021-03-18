package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkData;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import com.infinityraider.agricraft.capability.CapabilityMultiBlockData;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.DataHandler;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.function.Function;

public interface IProxy extends IProxyBase<Config> {

    @Override
    default Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Server::new;
    }

    @Override
    default void onCommonSetupEvent(final FMLCommonSetupEvent event) {
        PluginHandler.onCommonSetup(event);
    }

    @Override
    default void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        PluginHandler.onInterModEnqueueEvent(event);
    }

    @Override
    default void onInterModProcessEvent(final InterModProcessEvent event) {
        PluginHandler.onInterModProcessEvent(event);
    }

    @Override
    default void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        PluginHandler.populateRegistries();
    }

    @Override
    default void registerCapabilities() {
        this.registerCapability(CapabilityIrrigationNetworkComponent.getInstance());
        this.registerCapability(CapabilityIrrigationNetworkData.getInstance());
        this.registerCapability(CapabilityIrrigationNetworkManager.getInstance());
        this.registerCapability(CapabilityMultiBlockData.getInstance());
    }

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(CapabilityIrrigationNetworkManager.getInstance());
    }

    @Override
    default void registerFMLEventHandlers(IEventBus bus) {
        bus.addListener(DataHandler.getInstance()::onGatherDataEvent);
    }

    @Override
    default void activateRequiredModules() {}
    
    @Override
    default void onServerStartingEvent(final FMLServerStartingEvent event) {
        CoreHandler.init();
    }

    default void toggleSeedAnalyzerActive(boolean status) {}

    default void toggleSeedAnalyzerObserving(boolean status) {}
}
