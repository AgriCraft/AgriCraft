package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel<TileEntityChannelFull> {

	public RenderChannelFull(BlockWaterChannelFull block) {
		super(block, new TileEntityChannelFull());
	}

	@Override
	protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
		//draw bottom
		tessellator.drawScaledPrism(0, 0, 0, 16, 5, 16, matIcon);
		//draw top
		tessellator.drawScaledPrism(0, 12, 0, 16, 16, 16, matIcon);
		//draw four corners
		tessellator.drawScaledPrism(0, 5, 0, 5, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 0, 16, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 11, 16, 12, 16, matIcon);
		tessellator.drawScaledPrism(0, 5, 11, 5, 12, 16, matIcon);

	}

	@Override
	protected void renderSideRotated(ITessellator tessellator, TileEntityChannelFull channel, AgriForgeDirection dir, int code, TextureAtlasSprite matIcon) {
		if (code == 0) {
			tessellator.drawScaledPrism(5, 5, 0, 11, 12, 5, matIcon);
		}
	}
}
