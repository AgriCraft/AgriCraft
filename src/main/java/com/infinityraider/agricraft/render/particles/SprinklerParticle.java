package com.infinityraider.agricraft.render.particles;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SprinklerParticle extends AgriParticle implements IRenderUtilities {
    public SprinklerParticle(ClientWorld world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vector3d velocity) {
        this(world, fluid.getAttributes().getStillTexture(), x, y, z, scale, gravity, velocity);
    }

    public SprinklerParticle(ClientWorld world, ResourceLocation icon, double x, double y, double z, float scale, float gravity, Vector3d velocity) {
        super(world, x, y, z, scale, gravity, velocity, icon);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void renderParticle(@Nonnull IVertexBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks) {
        super.renderParticle(buffer, renderInfo, partialTicks);
    }

    @Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    private static final IParticleRenderType RENDER_TYPE = new IParticleRenderType() {
        @Override
        @SuppressWarnings("deprecation")
        public void beginRender(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            textureManager.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.alphaFunc(516, 0.003921569F);
            bufferBuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }

        public void finishRender(Tessellator tessellator) {
            tessellator.draw();
        }

        public String toString() {
            return "AGRI_SPRINKLER_PARTICLE";
        }
    };
}
