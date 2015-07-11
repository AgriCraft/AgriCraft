package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public final class RenderPlayerHooks {
    private HashMap<String, PlayerEffectRenderer> effectRenderers;
    private static boolean hasInit = false;

    public RenderPlayerHooks() {
        if(!hasInit) {
            hasInit = true;
            this.init();
        }
    }

    private void init() {
        this.registerPlayerEffectRenderer(new PlayerEffectRendererOrbs());
    }

    private void registerPlayerEffectRenderer(PlayerEffectRenderer renderer) {
        if(effectRenderers == null) {
            effectRenderers = new HashMap<String, PlayerEffectRenderer>();
        }
        for(String name:renderer.getDisplayNames()) {
            this.effectRenderers.put(name, renderer);
        }
    }

    @SubscribeEvent
    public void RenderPlayerEffects(RenderPlayerEvent.Post event) {
        if(effectRenderers.containsKey(event.entityPlayer.getDisplayName())) {
            if(!event.entityPlayer.isPotionActive(Potion.invisibility)) {
                effectRenderers.get(event.entityPlayer.getDisplayName()).renderEffects(event.entityPlayer, event.renderer, event.partialRenderTick);
            }
        }
    }
}
