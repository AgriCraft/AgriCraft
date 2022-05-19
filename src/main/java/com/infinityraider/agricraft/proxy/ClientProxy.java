package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.api.v1.client.AgriPlantRenderType;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.*;
import com.infinityraider.agricraft.impl.v1.JsonObjectFactory;
import com.infinityraider.agricraft.impl.v1.JsonObjectFactoryClient;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.render.blocks.BlockGreenHouseAirRenderer;
import com.infinityraider.agricraft.render.items.magnfiyingglass.MagnifyingGlassGenomeInspector;
import com.infinityraider.agricraft.render.items.magnfiyingglass.MagnifyingGlassSoilInspector;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IClientProxyBase<Config>, IProxy {
    @Override
    public Function<ForgeConfigSpec.Builder, Config> getConfigConstructor() {
        return Config.Client::new;
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        PluginHandler.onClientSetup(event);
        MagnifyingGlassGenomeInspector.init();
        MagnifyingGlassSoilInspector.init();
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(BlockGreenHouseAirRenderer.getInstance());
        this.registerEventHandler(ItemToolTipHandler.getInstance());
        this.registerEventHandler(JournalViewPointHandler.getInstance());
        this.registerEventHandler(MagnifyingGlassViewHandler.getInstance());
        this.registerEventHandler(PlayerLogOutHandler.getInstance());
        this.registerEventHandler(SeedAnalyzerViewPointHandler.getInstance());
        this.registerEventHandler(SeedBagScrollHandler.getInstance());
        this.registerEventHandler(SeedBagShakeHandler.getInstance());
    }

    @Override
    public void registerModBusEventHandlers(IEventBus bus) {
        IProxy.super.registerModBusEventHandlers(bus);
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
    public JsonObjectFactory jsonObjectFactory() {
        return JsonObjectFactoryClient.getInstance();
    }

    @Override
    public boolean isValidRenderType(String renderType) {
        return AgriPlantRenderType.fetchFromIdentifier(renderType).isPresent();
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
    public boolean toggleJournalObserving(Player player, InteractionHand hand) {
        return JournalViewPointHandler.getInstance().toggle(player, hand);
    }

    @Override
    public void toggleMagnifyingGlassObserving(InteractionHand hand) {
        MagnifyingGlassViewHandler.getInstance().toggle(hand);
    }

    @Override
    public boolean isMagnifyingGlassObserving(Player player) {
        if(player == this.getClientPlayer()) {
            return MagnifyingGlassViewHandler.getInstance().isActive() && MagnifyingGlassViewHandler.getInstance().isAnimationComplete();
        } else {
            return IProxy.super.isMagnifyingGlassObserving(player);
        }
    }

    @Override
    public int getParticleSetting() {
        return Minecraft.getInstance().options.particles.getId();
    }
}
