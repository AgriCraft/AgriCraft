package com.InfinityRaider.AgriCraft.renderers.player.renderhooks;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
