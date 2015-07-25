package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import com.InfinityRaider.AgriCraft.renderers.particles.DustFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererNavi extends PlayerEffectRenderer {
    private short counter = 0;
    private final Sphere sphere;
    private final float f;
    private final int detail;

    private final ResourceLocation texture;
    private final ResourceLocation largeWing;
    private final ResourceLocation smallWing;

    protected PlayerEffectRendererNavi() {
        super();

        largeWing = new ResourceLocation("agricraft", "textures/entities/player/navi/wingLarge.png");
        smallWing = new ResourceLocation("agricraft", "textures/entities/player/navi/wingSmall.png");
        texture = new ResourceLocation("agricraft", "textures/entities/player/navi/navi.png");

        f = 0.5F;
        detail = 8;
        sphere = new Sphere();
        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setOrientation(GLU.GLU_OUTSIDE);
    }

    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("SkeletonPunk");
        return list;
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float partialTick) {
        double[] pos = getPosition(player, partialTick);
        GL11.glTranslated(pos[0], pos[1], pos[2]);
        renderWings();
        renderSphere();
        spawnParticles(player, pos[1], partialTick);
        GL11.glTranslated(-pos[0], -pos[1], -pos[2]);
    }

    private double[] getPosition(EntityPlayer player, float partialTick) {
        double arg = Math.toRadians(360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double vY = 5;
        double aY = 0.1F;
        double dy = aY*Math.cos((vY*arg)%360);
        return new double[] {0.5F, -1+dy, 1};
    }

    private void renderSphere() {
        float scale = 0.2F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glScalef(scale, scale, scale);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        sphere.draw(f, detail, detail);
    }

    private void renderWings() {
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        float arg = (float) Math.toRadians((20*360*(System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL)%360);
        float sin = (float) (10*Math.sin(arg));
        float scale = 0.5F;

        GL11.glRotatef(90, 0, 1, 0);

        Minecraft.getMinecraft().renderEngine.bindTexture(largeWing);
        GL11.glRotatef(45 + sin, 0, 1, 0);
        draw(tessellator, scale);
        GL11.glRotatef(2 * (-45 - sin), 0, 1, 0);
        draw(tessellator, scale);
        GL11.glTranslatef(0, 0.25F, 0);
        Minecraft.getMinecraft().renderEngine.bindTexture(smallWing);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        //GL11.glRotatef(180, 1, 0, 0);
        draw(tessellator, scale);
        GL11.glRotatef(2 * (45 + sin), 0, 1, 0);
        draw(tessellator, scale);
        //GL11.glRotatef(180, -1, 0, 0);
        GL11.glScalef(2F, 2F, 2F);
        GL11.glRotatef(-45 - sin, 0, 1, 0);
        GL11.glTranslatef(0, -0.25F, 0);
    }

    private void draw(Tessellator tessellator, float scale) {
        //x-, y & z-axis
        //this.drawAxisSystem();

        GL11.glRotatef(-0, 1, 0, 1);
        tessellator.startDrawingQuads();

        //front
        tessellator.addVertexWithUV(0, 0, 0, 0, 1);
        tessellator.addVertexWithUV(0, -scale, 0, 0, 0);
        tessellator.addVertexWithUV(-scale, -scale, 0, 1, 0);
        tessellator.addVertexWithUV(-scale, 0, 0, 1, 1);
        //back
        tessellator.addVertexWithUV(-scale, 0, 0, 1, 1);
        tessellator.addVertexWithUV(-scale, -scale, 0, 1, 0);
        tessellator.addVertexWithUV(0, -scale, 0, 0, 0);

        tessellator.draw();
        GL11.glRotatef(0, 1, 0, 1);
    }

    private void spawnParticles(EntityPlayer player, double y, float partialTick) {
        short delay = 20;
        counter++;
        if (counter>=delay) {
            counter = 0;
            double x = 0.5F;
            y = y + (player==Minecraft.getMinecraft().thePlayer?0.62:0);
            double z = -1;
            double yaw = Math.toRadians(player.prevRenderYawOffset + (player.renderYawOffset-player.prevRenderYawOffset)*partialTick);
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            double xNew = x*cos - z*sin;
            double zNew = x*sin + z*cos;
            Vec3 vector = Vec3.createVectorHelper(0, 0, 0);
            float scale = player.worldObj.rand.nextFloat();
            double radius = 0.3*player.worldObj.rand.nextDouble();
            double angle = Math.toRadians(player.worldObj.rand.nextInt(360));
            DustFX particle = new DustFX(player.worldObj, player.posX+xNew+radius*Math.cos(angle), player.posY-2*y, +player.posZ+zNew+radius*Math.sin(angle), scale, 0.01F, vector, texture);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}

