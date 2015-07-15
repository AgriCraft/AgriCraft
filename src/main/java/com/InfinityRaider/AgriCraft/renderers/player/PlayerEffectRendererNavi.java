package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererNavi extends PlayerEffectRenderer {
    private final int sphereId;
    private final Sphere sphere;
    private final float f;
    private final int detail;

    PlayerEffectRendererNavi() {
        super();
        f = 0.5F;
        detail = 8;
        sphere = new Sphere();
        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setOrientation(GLU.GLU_OUTSIDE);
        sphereId = GL11.glGenLists(1);

        GL11.glNewList(sphereId, GL11.GL_COMPILE);
        ResourceLocation texture = new ResourceLocation("agricraft", "textures/entities/player/navi.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        sphere.draw(f, detail, detail);
        GL11.glEndList();
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
        renderSphere(pos[0], pos[1], pos[2]);
    }

    private double[] getPosition(EntityPlayer player, float partialTick) {
        double arg = Math.toRadians (360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double vY = 5;
        double aY = 0.1F;
        double dy = aY*Math.cos((vY*arg)%360);
        double[] pos = new double[] {0.5F, -1+dy, 1};
        return pos;

    }

    private void renderSphere(double x, double y, double z) {
        float scale = 0.2F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glTranslated(x, y, z);
        GL11.glScalef(scale, scale, scale);
        GL11.glCallList(sphereId);
    }
}
