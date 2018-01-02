package com.infinityraider.agricraft.renderers.particles;

import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AgriCraftFX extends Particle {

    protected final ResourceLocation texture;

    protected AgriCraftFX(World world, double x, double y, double z, float scale, float gravity, Vec3d vector, ResourceLocation texture) {
        super(world, x, y, z, 0, 0, 0);
        this.texture = texture;
        this.particleScale = scale;
        this.motionX = vector.x;
        this.motionY = vector.y;
        this.motionZ = vector.z;
    }

    protected AgriCraftFX(World world, double x, double y, double z, float scale, float gravity, Vec3d vector, TextureAtlasSprite icon) {
        super(world, x, y, z, 0, 0, 0);
        this.texture = null;
        this.setParticleTexture(icon);
        this.particleGravity = gravity;
        this.particleScale = scale;
        this.motionX = vector.x;
        this.motionY = vector.y;
        this.motionZ = vector.z;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void renderParticle(BufferBuilder worldRenderer, Entity entity, float partialTicks, float f0, float f1, float f2, float f3, float f4) {
        //I'm doing this because else the textures blink and are fucked up and I have no idea how to fix it,
        //if anyone sees this and knows how, let me know please, thanks :D
        ITessellator tessellator = TessellatorVertexBuffer.getInstance(worldRenderer);

        if (texture != null) {
            float f6 = 0;
            float f7 = 1;
            float f8 = 0;
            float f9 = 1;
            float f10 = 0.1F * this.particleScale;
            float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) f0 - interpPosX);
            float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) f0 - interpPosY);
            float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) f0 - interpPosZ);
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
            tessellator.setColorRGBA(1, 1, 1, 0.5F);
            tessellator.addVertexWithUV((f11 - f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 - f3 * f10 - f6 * f10), f7, f9);
            tessellator.addVertexWithUV((f11 - f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 - f3 * f10 + f6 * f10), f7, f8);
            tessellator.addVertexWithUV((f11 + f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 + f3 * f10 + f6 * f10), f6, f8);
            tessellator.addVertexWithUV((f11 + f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 + f3 * f10 - f6 * f10), f6, f9);
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            super.renderParticle(worldRenderer, entity, partialTicks, f0, f1, f2, f3, f4);
        }

    }
}
