package com.infinityraider.agricraft.client.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.client.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelFull;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.client.renderers.RenderUtil.*;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel {

	public RenderChannelFull() {
		super(AgriCraftBlocks.blockWaterChannelFull, new TileEntityChannelFull());
	}

	@Override
	protected void renderBottom(TessellatorV2 tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm) {
		//draw bottom
		drawScaledPrism(tessellator, 0, 0, 0, 16, 5, 16, matIcon, cm);
		//draw top
		drawScaledPrism(tessellator, 0, 12, 0, 16, 16, 16, matIcon, cm);
		//draw four corners
		drawScaledPrism(tessellator, 0, 5, 0, 5, 12, 5, matIcon, cm);
		drawScaledPrism(tessellator, 11, 5, 0, 16, 12, 5, matIcon, cm);
		drawScaledPrism(tessellator, 11, 5, 11, 16, 12, 16, matIcon, cm);
		drawScaledPrism(tessellator, 0, 5, 11, 5, 12, 16, matIcon, cm);

	}

	//renders one of the four sides of a channel
	// So tiny!
	@Override
	protected void renderSide(TessellatorV2 tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm, AgriForgeDirection dir) {
		if (!channel.hasNeighbourCheck(dir)) {
			drawScaledPrism(tessellator, 5, 5, 0, 11, 12, 5, matIcon, cm, dir);
		}
	}
}
