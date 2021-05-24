package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.*;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
        this.registerEventHandler(JournalViewPointHandler.getInstance());
        this.registerEventHandler(MagnifyingGlassViewHandler.getInstance());
        this.registerEventHandler(SeedAnalyzerViewPointHandler.getInstance());
        this.registerEventHandler(SeedBagScrollHandler.getInstance());
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
        ModuleKeyboard.getInstance().activate();
    }

    @Override
    public void toggleSeedAnalyzerActive(boolean status) {
        SeedAnalyzerViewPointHandler.getInstance().setActive(status);
    }

    @Override
    public void toggleSeedAnalyzerObserving(boolean status) {
        SeedAnalyzerViewPointHandler.getInstance().setObserved(status);
    }

    @Override
    public boolean toggleJournalObserving(ItemStack journal, Hand hand) {
        return JournalViewPointHandler.getInstance().toggle(journal, hand);
    }

    @Override
    public void toggleMagnifyingGlassObserving(Hand hand) {
        MagnifyingGlassViewHandler.getInstance().toggle(hand);
    }

    @Override
    public boolean isMagnifyingGlassObserving(PlayerEntity player) {
        if(player == this.getClientPlayer()) {
            return MagnifyingGlassViewHandler.getInstance().isActive() && MagnifyingGlassViewHandler.getInstance().isAnimationComplete();
        } else {
            return IProxy.super.isMagnifyingGlassObserving(player);
        }
    }

    @Override
    public int getParticleSetting() {
        return Minecraft.getInstance().gameSettings.particles.getId();
    }
}
