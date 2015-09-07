package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannelFull;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel {

    public RenderChannelFull() {
        super(Blocks.blockWaterChannelFull, new TileEntityChannelFull());
    }

    @Override
    protected void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        //the texture
        IIcon icon = channel.getIcon();
        int cm = channel.colorMultiplier();
        //draw bottom
        drawScaledPrism(tessellator, 0, 0, 0, 16, 5, 16, icon, cm);
        //draw top
        drawScaledPrism(tessellator, 0, 12, 0, 16, 16, 16, icon, cm);
        //draw four corners
        drawScaledPrism(tessellator, 0, 5, 0, 5, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 11, 5, 0, 16, 12, 5, icon, cm);
        drawScaledPrism(tessellator, 11, 5, 11, 16, 12, 16, icon, cm);
        drawScaledPrism(tessellator, 0, 5, 11, 5, 12, 16, icon, cm);

    }

    //renders one of the four sides of a channel
    @Override
    protected void renderSide(TileEntityChannel channel, Tessellator tessellator, char axis, int direction) {
        if((axis=='x' || axis=='z') && (direction==1 || direction==-1)) {
            //checks if there is a neighbouring block that this block can connect to
            boolean neighbour = channel.hasNeighbour(axis, direction);
            boolean x = axis == 'x';
            //the texture
            IIcon icon = channel.getIcon();
            int cm = channel.colorMultiplier();
            if(!neighbour) {
                if(x) {
                    drawScaledPrism(tessellator, direction==1?11:0, 5, 5, direction==1?16:5, 12, 11, icon, cm);
                } else {
                    drawScaledPrism(tessellator, 5, 5, direction==1?11:0, 11, 12, direction==1?16:5, icon, cm);
                }
            }
        }
    }
}
