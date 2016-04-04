package com.infinityraider.agricraft.client.renderers.playereffect;

import com.infinityraider.agricraft.client.renderers.TessellatorV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    void renderEffects(TessellatorV2 tessellator, EntityPlayer player, RenderPlayer renderer, float partialTick) {
        short delay = getSpawnDelay();
        counter++;
        if (counter >= delay) {
            Minecraft.getMinecraft().effectRenderer.addEffect(getParticle(player, partialTick));
            counter = 0;
        }
    }
}
