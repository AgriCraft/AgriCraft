package com.infinityraider.agricraft.renderers.particles;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract  class AgriCraftFX extends EntityFX {
    protected final ResourceLocation texture;

    protected AgriCraftFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector, ResourceLocation texture) {
        super(world, x, y, z, 0, 0, 0);
        this.texture = texture;
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.particleGravity = gravity;
        this.particleScale = scale;
        this.motionX = vector.xCoord;
        this.motionY = vector.yCoord;
        this.motionZ = vector.zCoord;
    }

    protected AgriCraftFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector, TextureAtlasSprite icon) {
        super(world, x, y, z, 0, 0, 0);
        this.texture = null;
        this.setParticleIcon(icon);
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.particleGravity = gravity;
        this.particleScale = scale;
        this.motionX = vector.xCoord;
        this.motionY = vector.yCoord;
        this.motionZ = vector.zCoord;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void renderParticle(WorldRenderer worldRenderer, Entity entity, float partialTicks, float f0, float f1, float f2, float f3, float f4) {
        //I'm doing this because else the textures blink and are fucked up and I have no idea how to fix it,
        //if anyone sees this and knows how, let me know please, thanks :D
        TessellatorV2 tessellator = TessellatorV2.getInstance(worldRenderer);
        tessellator.draw();
        tessellator.startDrawingQuads();

        if(texture != null) {
            float f6 = 0;
            float f7 = 1;
            float f8 = 0;
            float f9 = 1;
            float f10 = 0.1F * this.particleScale;
            float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) f0 - interpPosX);
            float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) f0 - interpPosY);
            float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) f0 - interpPosZ);
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
            tessellator.setColorRGBA_F(1, 1, 1, 0.5F);
            tessellator.addVertexWithUV((double) (f11 - f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 - f3 * f10 - f6 * f10), f7, f9);
            tessellator.addVertexWithUV((double) (f11 - f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 - f3 * f10 + f6 * f10), f7, f8);
            tessellator.addVertexWithUV((double) (f11 + f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 + f3 * f10 + f6 * f10), f6, f8);
            tessellator.addVertexWithUV((double) (f11 + f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 + f3 * f10 - f6 * f10), f6, f9);
        } else {
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            super.renderParticle(worldRenderer, entity, partialTicks, f0, f1, f2, f3, f4);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
    }
}
