package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.BaseIcons;
import net.minecraft.init.Blocks;

public class RenderBlockGrate extends RenderBlockCustomWood<TileEntityGrate> {

	public RenderBlockGrate() {
		super(AgriCraftBlocks.blockGrate, new TileEntityGrate(), true, false, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {
		drawScaledPrism(tess, 7, 0, 1, 9, 16, 3, matIcon);
		drawScaledPrism(tess, 7, 0, 5, 9, 16, 7, matIcon);
		drawScaledPrism(tess, 7, 0, 9, 9, 16, 11, matIcon);
		drawScaledPrism(tess, 7, 0, 13, 9, 16, 15, matIcon);
		drawScaledPrism(tess, 7, 1, 0, 9, 3, 16, matIcon);
		drawScaledPrism(tess, 7, 5, 0, 9, 7, 16, matIcon);
		drawScaledPrism(tess, 7, 9, 0, 9, 11, 16, matIcon);
		drawScaledPrism(tess, 7, 13, 0, 9, 15, 16, matIcon);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityGrate) {

			// Setup
			final TileEntityGrate grate = (TileEntityGrate) te;
			final float offset = ((float) grate.getOffset() * 7) / 16.0F;

			// Offset
			tess.translate(0, 0, offset);

			// Draw Grate
			drawScaledPrism(tess, 1, 0, 0, 3, 16, 2, matIcon, cm);
			drawScaledPrism(tess, 5, 0, 0, 7, 16, 2, matIcon, cm);
			drawScaledPrism(tess, 9, 0, 0, 11, 16, 2, matIcon, cm);
			drawScaledPrism(tess, 13, 0, 0, 15, 16, 2, matIcon, cm);
			drawScaledPrism(tess, 0, 1, 0, 16, 3, 2, matIcon, cm);
			drawScaledPrism(tess, 0, 5, 0, 16, 7, 2, matIcon, cm);
			drawScaledPrism(tess, 0, 9, 0, 16, 11, 2, matIcon, cm);
			drawScaledPrism(tess, 0, 13, 0, 16, 15, 2, matIcon, cm);

			//vines
			final TextureAtlasSprite vinesIcon = BaseIcons.VINE.getIcon();
			int l = Blocks.vine.colorMultiplier(world, pos);
			float f0 = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			tess.setColorOpaque_F(f0, f1, f2);
			
			if (grate.hasVines(true)) {
				drawScaledFaceDoubleXY(tess, 0, 0, 16, 16, vinesIcon, 0.001f);
			}
			if (grate.hasVines(false)) {
				drawScaledFaceDoubleXY(tess, 0, 0, 16, 16, vinesIcon, 1.999f);
			}
		}
	}

}
