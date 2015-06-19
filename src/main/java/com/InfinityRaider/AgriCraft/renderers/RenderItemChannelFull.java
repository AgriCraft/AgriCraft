package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemChannelFull implements IItemRenderer {
    private TileEntity tileEntity;

    public RenderItemChannelFull(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        TileEntityChannel channel = (TileEntityChannel) tileEntity;
        channel.setMaterial(item.getTagCompound());
        this.renderModel(channel, 0.0, 0.0, 0.0, item.getItemDamage());
    }

    public void renderModel(TileEntityChannel channel, double x, double y, double z, int meta) {
        Tessellator tessellator = Tessellator.instance;
        //render the model
        GL11.glPushMatrix();
        //translate the matrix to the right spot
        GL11.glTranslated(x,y,z);
        //draw the tank
        if(meta==0) {
            this.drawWoodChannel(channel, tessellator);
        }
        GL11.glPopMatrix();
    }

    private void drawWoodChannel(TileEntityChannel channel, Tessellator tessellator) {
        //the texture
        IIcon icon = channel.getIcon();
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
        //draw first plane front
        //draw bottom
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 0, 16, 5, 16, icon);
        //draw top
        RenderHelper.drawScaledPrism(tessellator, 0, 12, 0, 16, 16, 16, icon);
        //draw four corners
        RenderHelper.drawScaledPrism(tessellator, 0, 5, 0, 5, 12, 5, icon);
        RenderHelper.drawScaledPrism(tessellator, 11, 5, 0, 16, 12, 5, icon);
        RenderHelper.drawScaledPrism(tessellator, 11, 5, 11, 16, 12, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 0, 5, 11, 5, 12, 16, icon);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
