package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public final class RenderPlayerHooks {
    private static HashMap<String, Class<? extends PlayerEffectRenderer>>  effectRenderers;

    private HashMap<String, PlayerEffectRenderer> activeEffectRenderers;
    private static boolean hasInit = false;

    public RenderPlayerHooks() {
        if(!hasInit) {
            hasInit = true;
            this.init();
        }
    }

    private void init() {
        this.registerPlayerEffectRenderer(new PlayerEffectRendererOrbs());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererNavi());
        this.registerPlayerEffectRenderer(new PlayerEffectRendererParticlesEnchanted());
    }

    private void registerPlayerEffectRenderer(PlayerEffectRenderer renderer) {
        if(activeEffectRenderers == null) {
            activeEffectRenderers = new HashMap<String, PlayerEffectRenderer>();
        }
        for(String name:renderer.getDisplayNames()) {
            this.activeEffectRenderers.put(name, renderer);
        }
    }

    @SubscribeEvent
    public void RenderPlayerEffects(RenderPlayerEvent.Specials.Post event) {
        if(activeEffectRenderers.containsKey(event.entityPlayer.getDisplayName())) {
            if(!event.entityPlayer.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
                GL11.glPushMatrix();
                activeEffectRenderers.get(event.entityPlayer.getDisplayName()).renderEffects(event.entityPlayer, event.renderer, event.partialRenderTick);
                GL11.glPopMatrix();
            }
        }
    }
}
