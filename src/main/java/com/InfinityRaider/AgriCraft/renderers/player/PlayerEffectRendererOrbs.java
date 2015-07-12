package com.InfinityRaider.AgriCraft.renderers.player;

import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererOrbs extends PlayerEffectRenderer {
    private static final double R = 1;
    private static final int MAX_BLURS = 5;

    private static final ResourceLocation quas = new ResourceLocation("agricraft", "textures/entities/player/quas.png");
    private static final ResourceLocation wex = new ResourceLocation("agricraft", "textures/entities/player/wex.png");
    private static final ResourceLocation exort = new ResourceLocation("agricraft", "textures/entities/player/exort.png");

    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Reference.AUTHOR);
        return list;
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float partialTick) {
        Tessellator tessellator = Tessellator.instance;
        rotateToGeneralCoordinates(player, partialTick);

        float dy = player.getEyeHeight()-(player!=Minecraft.getMinecraft().thePlayer?1.62F:0F)-0.25F;
        tessellator.addTranslation(0, -dy, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.05F);

        float newAngle = (float) (360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double tilt = Math.toRadians(45);

        for(int i=0;i<MAX_BLURS;i++) {
            GL11.glColor4f(1F, 1F, 1F, 0.7F*(1.0F - (i+0.0F)/MAX_BLURS));

            float angle = (10*(newAngle+360-(2.5F*i)/MAX_BLURS))%360;
            double a = Math.toRadians(-angle);
            double b = Math.toRadians(angle+120);
            double c = Math.toRadians(angle-120);

            float orb1X = (float) (R * Math.cos(a));
            float orb1Y = 0;
            float orb1Z = (float) (R * Math.sin(a));

            float orb2X = (float) (-R * Math.sin(b) * Math.sin(tilt));
            float orb2Y = (float) (R * Math.sin(b) * Math.cos(tilt));
            float orb2Z = (float) (R * Math.cos(b));

            float orb3X = (float) (-R * Math.sin(c) * Math.sin(-tilt));
            float orb3Y = (float) (R * Math.sin(c) * Math.cos(-tilt));
            float orb3Z = (float) (R * Math.cos(c));

            Minecraft.getMinecraft().renderEngine.bindTexture(exort);
            renderOrb(tessellator, orb1X, orb1Y, orb1Z, i);
            Minecraft.getMinecraft().renderEngine.bindTexture(wex);
            renderOrb(tessellator, orb2X, orb2Y, orb2Z, i);
            Minecraft.getMinecraft().renderEngine.bindTexture(quas);
            renderOrb(tessellator, orb3X, orb3Y, orb3Z, i);

        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        tessellator.addTranslation(0, dy, 0);
    }

    private void renderOrb(Tessellator tessellator, float x, float y, float z, int index) {
        float scale = 0.375F*(1.0F - 0.25F*(index+0.0F)/MAX_BLURS);
        GL11.glTranslatef(x, y, z);

        //make sure the texture always renders parallel to the player's screen
        GL11.glRotatef(RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);

        tessellator.startDrawingQuads();
            //front
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (-8), 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (8), 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (8), scale * 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (-8), scale * 16, 0, 16, 0);
            //back
            RenderHelper.addScaledVertexWithUV(tessellator, scale*(-8), 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, scale*(-8), scale*16, 0, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (8), scale * 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, scale * (8), 0, 0, 0, 16);
        tessellator.draw();

        GL11.glRotatef(-RenderManager.instance.playerViewX, 1F, 0F, 0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(-x, -y, -z);
    }
}
