package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class PlayerEffectRendererParticles extends PlayerEffectRenderer {
    protected final ResourceLocation texture;
    private int counter = 0;

    protected PlayerEffectRendererParticles() {
        this.texture = getParticleTexture();
    }

    protected abstract ResourceLocation getParticleTexture();

    protected abstract short getSpawnDelay();

    protected abstract EntityFX getParticle(EntityPlayer player, float partialTick);

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float partialTick) {
        short delay = getSpawnDelay();
        counter++;
        if (counter >= delay) {
            Minecraft.getMinecraft().effectRenderer.addEffect(getParticle(player, partialTick));
            counter = 0;
        }
    }
}
