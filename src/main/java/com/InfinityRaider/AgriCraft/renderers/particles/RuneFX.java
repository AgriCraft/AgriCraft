package com.InfinityRaider.AgriCraft.renderers.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class RuneFX extends AgriCraftFX {
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;

    public RuneFX(World world, double x, double y, double z, float gravity, Vec3 vector, ResourceLocation texture, float uMin, float vMin, float uMax, float vMax) {
        super(world, x, y, z, 1.0F, gravity, vector, texture);
        this.particleMaxAge = 100;
        this.setSize(1f, 1f);
        this.noClip = false;
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f0, float f1, float f2, float f3, float f4, float f5) {
        tessellator.draw();
        tessellator.startDrawingQuads();

        float f6 = uMin;
        float f7 = uMax;
        float f8 = vMin;
        float f9 = vMax;
        float f10 = 0.1F * this.particleScale;
        float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) f0 - interpPosX);
        float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) f0 - interpPosY);
        float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) f0 - interpPosZ);

        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        tessellator.addVertexWithUV((double) (f11 - f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 - f3 * f10 - f6 * f10), (double) f7, (double) f9);
        tessellator.addVertexWithUV((double) (f11 - f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 - f3 * f10 + f6 * f10), (double) f7, (double) f8);
        tessellator.addVertexWithUV((double) (f11 + f1 * f10 + f4 * f10), (double) (f12 + f2 * f10), (double) (f13 + f3 * f10 + f6 * f10), (double) f6, (double) f8);
        tessellator.addVertexWithUV((double) (f11 + f1 * f10 - f4 * f10), (double) (f12 - f2 * f10), (double) (f13 + f3 * f10 - f6 * f10), (double) f6, (double) f9);

        tessellator.draw();
        tessellator.startDrawingQuads();
    }
}
