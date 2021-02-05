package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.ItemToolTipHandler;
import com.infinityraider.agricraft.handler.ModelAndTextureHandler;
import com.infinityraider.agricraft.render.overlay.SeedAnalyzerOverlayRenderer;
import com.infinityraider.infinitylib.proxy.base.IClientProxyBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
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
        this.registerEventHandler(SeedAnalyzerOverlayRenderer.getInstance());
    }

    @Override
    public void registerFMLEventHandlers(IEventBus bus) {
        IProxy.super.registerFMLEventHandlers(bus);
        bus.addListener(ModelAndTextureHandler.getInstance()::onModelLoadEvent);
        bus.addListener(ModelAndTextureHandler.getInstance()::onTextureStitchEvent);
    }

    @Override
    public String translateToLocal(String string) {
        return I18n.format(string);
    }

    @Override
    public String getLocale() {
        return Minecraft.getInstance().getLanguageManager().getCurrentLanguage().getCode();
    }

    @Override
    public void updateSeedAnalyzerOverlay(BlockPos pos) {
        SeedAnalyzerOverlayRenderer.getInstance().onSeedAnalyzerUpdate(pos);
    }
}
