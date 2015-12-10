package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import com.InfinityRaider.AgriCraft.reference.Reference;
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
    private final double R;
    private final int MAX_BLURS;

    private final ResourceLocation quas;
    private final ResourceLocation wex;
    private final ResourceLocation exort;

    protected PlayerEffectRendererOrbs() {
        super();
        R = 1;
        MAX_BLURS = 5;
        quas = new ResourceLocation("agricraft", "textures/entities/player/invoker/quas.png");
        wex  = new ResourceLocation("agricraft", "textures/entities/player/invoker/wex.png");
        exort = new ResourceLocation("agricraft", "textures/entities/player/invoker/exort.png");
    }

    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(Reference.AUTHOR);
        return list;
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float partialTick) {
        //tessellator instance
        Tessellator tessellator = Tessellator.instance;

        //rotate the coordinate system to the minecraft (x,y,z) instead of the system attached to the player
        rotateToGeneralCoordinates(player, partialTick);

        //move origin to wanted height
        float dy = player.getEyeHeight()-(player!=Minecraft.getMinecraft().thePlayer?1.62F:0F)-0.25F;
        tessellator.addTranslation(0, -dy, 0);

        //set some GL settings
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.05F);

        //some angles for the trajectories
        float newAngle = (float) (360 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        double tilt = Math.toRadians(45);

        for(int i=0;i<MAX_BLURS;i++) {
            //make the trail more transparent near the end
            GL11.glColor4f(1F, 1F, 1F, 0.7F*(1.0F - (i+0.0F)/MAX_BLURS));

            //calculate trajectories
            float angle = (10*(newAngle+360-(2.5F*i)/MAX_BLURS))%360;
            double a = Math.toRadians(angle-120);
            double b = Math.toRadians(-angle+120);
            double c = Math.toRadians(angle);

            float orb1X = (float) (R * Math.cos(a));
            float orb1Y = 0;
            float orb1Z = (float) (R * Math.sin(a));

            float orb2X = (float) (-R * Math.sin(b) * Math.sin(tilt));
            float orb2Y = (float) (R * Math.sin(b) * Math.cos(tilt));
            float orb2Z = (float) (R * Math.cos(b));

            float orb3X = (float) (-R * Math.sin(c) * Math.sin(-tilt));
            float orb3Y = (float) (R * Math.sin(c) * Math.cos(-tilt));
            float orb3Z = (float) (R * Math.cos(c));

            //render the actual orbs
            Minecraft.getMinecraft().renderEngine.bindTexture(exort);
            renderOrb(tessellator, orb1X, orb1Y, orb1Z, i);
            Minecraft.getMinecraft().renderEngine.bindTexture(wex);
            renderOrb(tessellator, orb2X, orb2Y, orb2Z, i);
            Minecraft.getMinecraft().renderEngine.bindTexture(quas);
            renderOrb(tessellator, orb3X, orb3Y, orb3Z, i);
        }

        //reset GL settings
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        tessellator.addTranslation(0, dy, 0);
    }

    private void renderOrb(Tessellator tessellator, float x, float y, float z, int index) {
        //rescale the size of the orbs and make them smaller towards the end of the trail
        float scale = 0.375F*(1.0F - 0.25F*(index+0.0F)/MAX_BLURS);

        //translate to the position where the orb has to be rendered
        GL11.glTranslatef(x, y, z);

        //rotate the coordinate system to make sure the texture always renders parallel to the player's screen
        GL11.glRotatef(RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);

        tessellator.startDrawingQuads();
            //front
            addScaledVertexWithUV(tessellator, scale * (-8), 0, 0, 16, 16);
            addScaledVertexWithUV(tessellator, scale * (8), 0, 0, 0, 16);
            addScaledVertexWithUV(tessellator, scale * (8), scale * 16, 0, 0, 0);
            addScaledVertexWithUV(tessellator, scale * (-8), scale * 16, 0, 16, 0);
            //back
            addScaledVertexWithUV(tessellator, scale*(-8), 0, 0, 16, 16);
            addScaledVertexWithUV(tessellator, scale*(-8), scale*16, 0, 16, 0);
            addScaledVertexWithUV(tessellator, scale * (8), scale * 16, 0, 0, 0);
            addScaledVertexWithUV(tessellator, scale * (8), 0, 0, 0, 16);
        tessellator.draw();

        //rotate the coordinate system back after rendering this orb
        GL11.glRotatef(-RenderManager.instance.playerViewX, 1F, 0F, 0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

        //translate back to the player after rendering
        GL11.glTranslatef(-x, -y, -z);
    }
}
