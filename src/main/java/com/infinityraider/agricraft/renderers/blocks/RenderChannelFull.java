package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelFull;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel {

    public RenderChannelFull() {
        super(AgriCraftBlocks.blockWaterChannelFull, new TileEntityChannelFull());
    }

    @Override
    protected void renderBottom(TileEntityChannel channel, TessellatorV2 tessellator) {
        //the texture
        TextureAtlasSprite icon = channel.getIcon();
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
    protected void renderSide(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection dir) {
		// the texture
		TextureAtlasSprite icon = channel.getIcon();
		int cm = channel.colorMultiplier();
		if (!channel.hasNeighbourCheck(dir)) {
			drawScaledPrism(tessellator, 5, 5, 0, 11, 12, 5, icon, cm, dir);
		}
    }
}
