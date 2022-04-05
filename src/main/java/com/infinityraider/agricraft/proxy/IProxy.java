package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.capability.*;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.handler.*;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

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
    @SuppressWarnings("deprecation")
    default void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        PluginHandler.populateRegistries();
    }

    @Override
    default void registerCapabilities() {
        this.registerCapability(CapabilityCrop.getInstance());
        this.registerCapability(CapabilityEatCropGoal.getInstance());
        this.registerCapability(CapabilityGeneInspector.getInstance());
        this.registerCapability(CapabilityGreenHouseData.getInstance());
        this.registerCapability(CapabilityJournalData.getInstance());
        this.registerCapability(CapabilityResearchedPlants.getInstance());
        this.registerCapability(CapabilitySeedBagContents.getInstance());
    }

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(AnvilHandler.getInstance());
        this.registerEventHandler(BlockUpdateHandler.getInstance());
        this.registerEventHandler(BonemealHandler.getInstance());
        this.registerEventHandler(JsonSyncHandler.getInstance());
        this.registerEventHandler(SeedBagEnchantingHandler.getInstance());
        this.registerEventHandler(PlayerConnectToServerHandler.getInstance());
        this.registerEventHandler(VanillaSeedConversionHandler.getInstance());
    }

    @Override
    default void registerModBusEventHandlers(IEventBus bus) {
        bus.addListener(DataHandler.getInstance()::onGatherDataEvent);
    }

    @Override
    default void activateRequiredModules() {}

    @Override
    default void onServerStartingEvent(final ServerStartingEvent event) {
        CoreHandler.init();
    }

    default boolean isValidRenderType(String renderType) {
        return true;
    }

    default void toggleSeedAnalyzerActive(boolean status) {}

    default void toggleSeedAnalyzerObserving(boolean status) {}

    default boolean toggleJournalObserving(Player player, InteractionHand hand) {
        return false;
    }

    default void toggleMagnifyingGlassObserving(InteractionHand hand) {}

    default boolean isMagnifyingGlassObserving(Player player) {
        return ItemMagnifyingGlass.isObserving(player);
    }

    default int getParticleSetting() {
        return 0;
    }
}
