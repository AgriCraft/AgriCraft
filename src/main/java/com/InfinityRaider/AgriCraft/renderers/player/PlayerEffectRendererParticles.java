package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class PlayerEffectRendererParticles extends PlayerEffectRenderer {
    protected abstract ResourceLocation getParticleTexture();

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick) {

    }
}
