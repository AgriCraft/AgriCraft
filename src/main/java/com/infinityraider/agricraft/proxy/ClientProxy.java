package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.ModelAndTextureHandler;
import com.infinityraider.agricraft.handler.SeedAnalyzerViewPointHandler;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IClientProxyBase<Config>, IProxy {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Client::new;
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(ItemToolTipHandler.getInstance());
        this.registerEventHandler(SeedAnalyzerViewPointHandler.getInstance());
    }

    @Override
    public void registerFMLEventHandlers(IEventBus bus) {
        IProxy.super.registerFMLEventHandlers(bus);
        bus.addListener(ModelAndTextureHandler.getInstance()::onModelLoadEvent);
        bus.addListener(ModelAndTextureHandler.getInstance()::onTextureStitchEvent);
    }

    @Override
    public void activateRequiredModules() {
        IProxy.super.activateRequiredModules();
        ModuleDynamicCamera.getInstance().activate();
    }

    @Override
    public void toggleSeedAnalyzerActive(boolean status) {
        SeedAnalyzerViewPointHandler.getInstance().setActive(status);
    }

    @Override
    public void toggleSeedAnalyzerObserving(boolean status) {
        SeedAnalyzerViewPointHandler.getInstance().setObserved(status);
    }
}
