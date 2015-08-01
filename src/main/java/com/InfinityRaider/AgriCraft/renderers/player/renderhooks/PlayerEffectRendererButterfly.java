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

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererButterfly extends PlayerEffectRenderer {
    private short counter = 0;
    private final ResourceLocation texture;
    private final ResourceLocation sparkle;

    protected PlayerEffectRendererButterfly() {
        super();
        texture = new ResourceLocation("agricraft", "textures/entities/player/butterfly/butterfly.png");
        sparkle = new ResourceLocation("agricraft", "textures/entities/player/butterfly/sparkle.png");
    }

    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("ValsNoisyToys");
        return names;
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float partialTick) {
        double[] pos = getPosition();
        GL11.glTranslated(pos[0], pos[1], pos[2]);
        renderWings();
        spawnParticles(player, pos[1], partialTick);
        GL11.glTranslated(-pos[0], -pos[1], -pos[2]);
    }

    private double[] getPosition() {
        double arg = Math.toRadians(360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double vY = 10;
        double aY = 0.1F;
        double dy = aY*Math.cos(vY*arg);
        return new double[] {0, -1+dy, 0.5F};
    }

    private void renderWings() {
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        float arg = (float) Math.toRadians((40*360*(System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL)%360);
        float sin = (float) (20*Math.sin(arg));
        float scale = 0.25F;

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glRotatef(-45, 0, 0, 1);
        GL11.glRotatef(90, 1, 0, 0);

        GL11.glRotatef(45 + sin, 1, 0, 0);
        draw(tessellator, scale);
        GL11.glRotatef(2 * (-45 - sin), 1, 0, 0);
        draw(tessellator, scale);
        GL11.glRotatef(45 + sin, 1, 0, 0);

        GL11.glRotatef(-90, 1, 0, 0);
        GL11.glRotatef(45, 0, 0, 1);
        GL11.glRotatef(-90, 0, 1, 0);

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void draw(Tessellator tessellator, float scale) {
        //x-, y & z-axis
        //this.drawAxisSystem();

        tessellator.startDrawingQuads();

        //front
        tessellator.addVertexWithUV(0, 0, 0, 0, 1);
        tessellator.addVertexWithUV(0, 0, scale, 1, 1);
        tessellator.addVertexWithUV(scale, 0, scale, 1, 0);
        tessellator.addVertexWithUV(scale, 0, 0, 0, 0);
        //bac
        tessellator.addVertexWithUV(0, 0, 0, 0, 1);
        tessellator.addVertexWithUV(scale, 0, 0, 0, 0);
        tessellator.addVertexWithUV(scale, 0, scale, 1, 0);
        tessellator.addVertexWithUV(0, 0, scale, 1, 1);

        tessellator.draw();
    }

    private void spawnParticles(EntityPlayer player, double y, float partialTick) {
        short delay = 20;
        counter++;
        if (counter>=delay) {
            counter = 0;
            double x = 0;
            y = y + (player==Minecraft.getMinecraft().thePlayer?0.62:0);
            double z = -0.5;
            double yaw = Math.toRadians(player.prevRenderYawOffset + (player.renderYawOffset-player.prevRenderYawOffset)*partialTick);
            double cos = Math.cos(yaw);
            double sin = Math.sin(yaw);
            double xNew = x*cos - z*sin;
            double zNew = x*sin + z*cos;
            Vec3 vector = Vec3.createVectorHelper(0, 0, 0);
            float scale = player.worldObj.rand.nextFloat();
            double radius = 0.3*player.worldObj.rand.nextDouble();
            double angle = Math.toRadians(player.worldObj.rand.nextInt(360));
            DustFX particle = new DustFX(player.worldObj, player.posX+xNew+radius*Math.cos(angle), player.posY-2*y, +player.posZ+zNew+radius*Math.sin(angle), scale, 0.01F, vector, sparkle);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}
