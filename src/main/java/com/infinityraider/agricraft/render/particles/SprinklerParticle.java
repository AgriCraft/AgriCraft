package com.infinityraider.agricraft.render.particles;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SprinklerParticle extends AgriParticle implements IRenderUtilities {
    public SprinklerParticle(ClientLevel world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
        super(world, x, y, z, scale, gravity, velocity, fluid.getAttributes().getStillTexture());
        this.setColor(fluid);
    }

    protected void setColor(Fluid fluid) {
        int color = fluid.getAttributes().getColor();
        this.setColor(
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                ((color) & 0xFF) / 255.0F);
        this.setAlpha(((color >> 24) & 0xFF) / 255.0F);
    }



    @Override
    @SuppressWarnings("deprecation")
    public void render(@Nonnull VertexConsumer buffer, @Nonnull Camera renderInfo, float partialTicks) {
        super.render(buffer, renderInfo, partialTicks);
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            textureManager.bindForSetup(InventoryMenu.BLOCK_ATLAS);
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "AGRI_SPRINKLER_PARTICLE";
        }
    };
}
