package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
abstract class PlayerEffectRenderer {
    PlayerEffectRenderer() {

    }

    abstract ArrayList<String> getDisplayNames();

    abstract void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick);

    protected void rotateToGeneralCoordinates(EntityPlayer player, float partialTick) {
        float yaw = player.prevRenderYawOffset + (player.renderYawOffset-player.prevRenderYawOffset)*partialTick;
        GL11.glRotatef(-yaw, 0, 1, 0);
    }

    /**
     * Adds a vertex to the tessellator scaled to the unit size of a block.
     *
     * @param tessellator the Tessellator instance used for rendering
     * @param x the x position, from 0 to 1.
     * @param y the y position, from 0 to 1.
     * @param z the z position, from 0 to 1.
     * @param u u offset for the bound texture
     * @param v v offset for the bound texture
     */
    protected void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        tessellator.addVertexWithUV(x * Constants.UNIT, y * Constants.UNIT, z * Constants.UNIT, u * Constants.UNIT, v * Constants.UNIT);
    }

    /**
     * utility method used for debugging rendering
     */
    @SuppressWarnings("unused")
    protected void drawAxisSystem() {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(-0.005F, 2, 0, 1, 0);
        tessellator.addVertexWithUV(0.005F, 2, 0, 0, 0);
        tessellator.addVertexWithUV(0.005F, -1, 0, 0, 1);
        tessellator.addVertexWithUV(-0.005F, -1, 0, 1, 1);

        tessellator.addVertexWithUV(2, -0.005F, 0, 1, 0);
        tessellator.addVertexWithUV(2, 0.005F, 0, 0, 0);
        tessellator.addVertexWithUV(-1, 0.005F, 0, 0, 1);
        tessellator.addVertexWithUV(-1, -0.005F, 0, 1, 1);

        tessellator.addVertexWithUV(0, -0.005F, 2, 1, 0);
        tessellator.addVertexWithUV(0, 0.005F, 2, 0, 0);
        tessellator.addVertexWithUV(0, 0.005F, -1, 0, 1);
        tessellator.addVertexWithUV(0, -0.005F, -1, 1, 1);

        tessellator.draw();
    }
}
