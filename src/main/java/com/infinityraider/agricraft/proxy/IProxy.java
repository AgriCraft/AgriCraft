package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.capability.*;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.handler.*;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
    @SuppressWarnings("deprecation")
    default void onModLoadCompleteEvent(final FMLLoadCompleteEvent event) {
        PluginHandler.populateRegistries();
    }

    @Override
    default void registerCapabilities() {
        this.registerCapability(CapabilityCrop.getInstance());
        this.registerCapability(CapabilityIrrigationContent.getInstance());
        this.registerCapability(CapabilityIrrigationComponent.getInstance());
        this.registerCapability(CapabilityEatCropGoal.getInstance());
        this.registerCapability(CapabilityResearchedPlants.getInstance());
        this.registerCapability(CapabilitySeedBagContents.getInstance());
    }

    @Override
    default void registerEventHandlers() {
        this.registerEventHandler(IrrigationSystemHandler.getInstance());
        this.registerEventHandler(BlockUpdateHandler.getInstance());
        this.registerEventHandler(SeedBagEnchantingHandler.getInstance());
        this.registerEventHandler(PlayerConnectToServerHandler.getInstance());
        this.registerEventHandler(VanillaPlantingHandler.getInstance());
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

    default boolean toggleJournalObserving(ItemStack journal, Hand hand) {
        return false;
    }

    default void toggleMagnifyingGlassObserving(Hand hand) {}

    default boolean isMagnifyingGlassObserving(PlayerEntity player) {
        return ItemMagnifyingGlass.isObserving(player);
    }
}
