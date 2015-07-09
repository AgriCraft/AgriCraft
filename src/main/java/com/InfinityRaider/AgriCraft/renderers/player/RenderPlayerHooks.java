package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
        this.registerPlayerEffectRenderer(new PlayerEffectRendererInfinityRaider());
    }

    private void registerPlayerEffectRenderer(PlayerEffectRenderer renderer) {
        this.effectRenderers.put(renderer.getUUID(), renderer);
    }

    @SubscribeEvent
    public void RenderPlayerEffects(RenderPlayerEvent.Post event) {
        if(effectRenderers.containsKey(event.entityPlayer.getUniqueID())) {
            effectRenderers.get(event.entityPlayer.getUniqueID()).renderEffects(event.entityPlayer, event.renderer, event.partialRenderTick);
        }
    }


}
