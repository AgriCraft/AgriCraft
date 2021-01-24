package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.infinitylib.proxy.base.IProxyBase;
import net.minecraftforge.common.ForgeConfigSpec;
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
    default void registerCapabilities() {}

    @Override
    default void registerEventHandlers() {}

    @Override
    default void activateRequiredModules() {}

    default String translateToLocal(String string) {
        // The {**} is a hack to get TOP integration to work.
        return "{*" + string + "*}";
    }

    default String getLocale() {
        // Whatever...
        return "en_us";
    }
    
    @Override
    default void onServerStartingEvent(final FMLServerStartingEvent event) {
        CoreHandler.init();
        /*
        // This is to be moved to infinity lib in a future version, I would expect.
        AgriCore.getLogger("agricraft").info("Registering AgriCraft Commands.");
        ReflectionHelper.forEachValueIn(AgriCraft.instance.getModCommandRegistry(), ICommand.class, event::registerServerCommand);
         */
    }
}
