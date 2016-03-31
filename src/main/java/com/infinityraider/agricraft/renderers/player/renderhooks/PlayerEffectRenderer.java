package com.infinityraider.agricraft.renderers.player.renderhooks;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
abstract class PlayerEffectRenderer {

    PlayerEffectRenderer() {

    }

    abstract ArrayList<String> getDisplayNames();

    abstract void renderEffects(ITessellator tessellator, EntityPlayer player, RenderPlayer renderer, float tick);

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
    protected void addScaledVertexWithUV(ITessellator tessellator, float x, float y, float z, float u, float v) {
        tessellator.addVertexWithUV(x * Constants.UNIT, y * Constants.UNIT, z * Constants.UNIT, u * Constants.UNIT, v * Constants.UNIT);
    }
	
}
