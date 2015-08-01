package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemChannel implements IItemRenderer {
    private TileEntity tileEntity;

    public RenderItemChannel(TileEntity tileEntity) {
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
        else if(meta==1) {
            this.drawIronChannel(channel, tessellator);
        }
        GL11.glPopMatrix();
    }

    private void drawWoodChannel(TileEntityChannel channel, Tessellator tessellator) {
        this.renderBottom(channel, tessellator);
        this.renderSide(channel, tessellator, 'x', -1);
        this.renderSide(channel, tessellator, 'x', 1);
        this.renderSide(channel, tessellator, 'z', -1);
        this.renderSide(channel, tessellator, 'z', 1);
    }

    private void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        //bind the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();
            //draw first plane front
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 4, 4, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 5, 12, 4, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 12, 12, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 5, 4, 12, 4);
            //draw first plane back
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 4, 4, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4,4, 12, 4);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 4, 12, 12, 12);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 4, 12, 4, 12);
        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    //renders one of the four sides of a channel
    private void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            boolean x = axis == 'x';
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
            //disable lighting
            GL11.glDisable(GL11.GL_LIGHTING);
            //tell the tessellator to start drawing
            tessellator.startDrawingQuads();
                //draw edge front
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):4, 4, x?12:(8.5F+3.5F*direction), 4, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 4, x?4:(8.5F+3.5F*direction), 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(8.5F+3.5F*direction), 12, 4);
                //draw edge back
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, x?4:12, x?4:(7.5F+3.5F*direction), x?4:12, x?12:4);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):12, 4, x?12:(7.5F+3.5F*direction), 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, x?12:4, x?12:(7.5F+3.5F*direction), x?12:4, x?4:12);
                //draw edge top
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?4:(7.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?4:(7.5F+3.5F*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(7.5F+3.5F*direction):4, 12, x?12:(8.5F+3.5F*direction), x?(7.5F+3.5F*direction):4, x?12:(8.5F+3.5F*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?12:(8.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?12:(8.5F+3.5F*direction));
                RenderHelper.addScaledVertexWithUV(tessellator, x?(8.5F+3.5F*direction):12, 12, x?4:(7.5F+3.5F*direction), x?(8.5F+3.5F*direction):12, x?4:(7.5F+3.5F*direction));
            tessellator.draw();
            //enable lighting
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    private void drawIronChannel(TileEntityChannel channel, Tessellator tessellator) {
        Minecraft.getMinecraft().renderEngine.bindTexture(RenderHelper.getBlockResource(channel.getIcon()));
    }
}
