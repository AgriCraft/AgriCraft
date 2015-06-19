package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel {

    public RenderChannelFull() {}

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.channelFullId);
    }

    @Override
    protected void renderBottom(TileEntityChannel channel, Tessellator tessellator) {
        //the texture
        IIcon icon = channel.getIcon();
        //draw bottom
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 0, 16, 5, 16, icon);
        //draw top
        RenderHelper.drawScaledPrism(tessellator, 0, 12, 0, 16, 16, 16, icon);
        //draw four corners
        RenderHelper.drawScaledPrism(tessellator, 0, 5, 0, 5, 12, 5, icon);
        RenderHelper.drawScaledPrism(tessellator, 11, 5, 0, 16, 12, 5, icon);
        RenderHelper.drawScaledPrism(tessellator, 11, 5, 11, 16, 12, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 0, 5, 11, 5, 12, 16, icon);

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
            if(!neighbour) {
                if(x) {
                    RenderHelper.drawScaledPrism(tessellator, direction == 1 ? 11 : 0, 5, 5, direction == 1 ? 16 : 5, 12, 11, icon);
                } else {
                    RenderHelper.drawScaledPrism(tessellator, 5, 5, direction==1?11:0, 12, 11, direction==1?16:5, icon);
                }
            }
        }
    }
}
