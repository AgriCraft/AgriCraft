package com.infinityraider.agricraft.render.particles;

import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AgriCraftFX extends SpriteTexturedParticle implements IRenderUtilities {

    protected final ResourceLocation texture;

    protected AgriCraftFX(ClientWorld world, double x, double y, double z, float scale, float gravity, Vector3d vector, ResourceLocation texture) {
        super(world, x, y, z, 0, 0, 0);
        this.particleGravity = gravity;
        this.multiplyParticleScaleBy(scale);
        this.texture = texture;
        this.motionX = vector.x;
        this.motionY = vector.y;
        this.motionZ = vector.z;
    }

    protected AgriCraftFX(ClientWorld world, double x, double y, double z, float scale, float gravity, Vector3d vector, TextureAtlasSprite icon) {
        super(world, x, y, z, 0, 0, 0);
        this.texture = null;
        this.setSprite(icon);
        this.particleGravity = gravity;
        this.multiplyParticleScaleBy(scale);
        this.motionX = vector.x;
        this.motionY = vector.y;
        this.motionZ = vector.z;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        if (texture != null) {
            this.bindTexture(this.texture);
        } else {
           this.bindTextureAtlas();
        }
        super.renderParticle(buffer, renderInfo, partialTicks);
    }

    @Override
    protected float getMinU() {
        return this.texture == null ? super.getMinU() : 0;
    }

    @Override
    protected float getMaxU() {
        return this.texture == null ? super.getMaxU() : 1;
    }

    @Override
    protected float getMinV() {
        return this.texture == null ? super.getMinV() : 0;
    }

    @Override
    protected float getMaxV() {
        return this.texture == null ? super.getMaxV() : 1;
    }
}
