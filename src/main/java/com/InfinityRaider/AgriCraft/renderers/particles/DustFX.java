package com.InfinityRaider.AgriCraft.renderers.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class DustFX extends EntityFX {
    private final ResourceLocation texture;
    public DustFX(World world, double x, double y, double z, float scale, float gravity, Vec3 vector, ResourceLocation texture) {
        super(world, x, y, z, 0 , 0, 0);
        this.texture = texture;
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.particleGravity = gravity;
        this.particleScale = scale;
        this.particleMaxAge = 50;
        this.setSize(0.2f, 0.2f);
        this.noClip = false;
        this.motionX = vector.xCoord;
        this.motionY = vector.yCoord;
        this.motionZ = vector.zCoord;
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f0, float f1, float f2, float f3, float f4, float f5) {
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
        tessellator.addVertexWithUV((double) (f11 - f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 - f3 * f10 - f6 * f10), (double) f7, (double) f9);
        tessellator.addVertexWithUV((double) (f11 - f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 - f3 * f10 + f6 * f10), (double) f7, (double) f8);
        tessellator.addVertexWithUV((double) (f11 + f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 + f3 * f10 + f6 * f10), (double) f6, (double) f8);
        tessellator.addVertexWithUV((double) (f11 + f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 + f3 * f10 - f6 * f10), (double) f6, (double) f9);
    }
}
