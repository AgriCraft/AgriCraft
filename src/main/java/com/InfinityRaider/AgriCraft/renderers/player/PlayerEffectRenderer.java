package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
}
