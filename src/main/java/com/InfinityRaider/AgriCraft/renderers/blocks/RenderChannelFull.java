package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannelFull;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

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
    // So tiny!
    @Override
    protected void renderSide(TileEntityChannel channel, Tessellator tessellator, ForgeDirection dir) {
		// the texture
		IIcon icon = channel.getIcon();
		int cm = channel.colorMultiplier();
		if (!channel.hasNeighbourCheck(dir)) {
			drawScaledPrism(tessellator, 5, 5, 0, 11, 12, 5, icon, cm, dir);
		}
    }
}
