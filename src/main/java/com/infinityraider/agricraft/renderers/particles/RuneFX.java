package com.infinityraider.agricraft.renderers.particles;

import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import com.infinityraider.infinitylib.render.tessellation.TessellatorVertexBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuneFX extends AgriCraftFX {

    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;

    public RuneFX(World world, double x, double y, double z, float gravity, Vec3d vector, ResourceLocation texture, float uMin, float vMin, float uMax, float vMax) {
        super(world, x, y, z, 1.0F, gravity, vector, texture);
        this.particleMaxAge = 100;
        this.setSize(1f, 1f);
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    @Override
    public void renderParticle(BufferBuilder worldRenderer, Entity entity, float partialTicks, float f0, float f1, float f2, float f3, float f4) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance(worldRenderer);
        tessellator.draw();
        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);

        float f6 = uMin;
        float f7 = uMax;
        float f8 = vMin;
        float f9 = vMax;
        float f10 = 0.1F * this.particleScale;
        float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) f0 - interpPosX);
        float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) f0 - interpPosY);
        float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) f0 - interpPosZ);

        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        tessellator.setColorRGBA(1, 1, 1, 1);

        tessellator.addVertexWithUV((f11 - f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 - f3 * f10 - f6 * f10), f7, f9);
        tessellator.addVertexWithUV((f11 - f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 - f3 * f10 + f6 * f10), f7, f8);
        tessellator.addVertexWithUV((f11 + f1 * f10 + f4 * f10), (f12 + f2 * f10), (f13 + f3 * f10 + f6 * f10), f6, f8);
        tessellator.addVertexWithUV((f11 + f1 * f10 - f4 * f10), (f12 - f2 * f10), (f13 + f3 * f10 - f6 * f10), f6, f9);

        tessellator.draw();
        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
    }
}
