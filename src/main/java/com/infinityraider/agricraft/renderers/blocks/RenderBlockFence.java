package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityFence;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;

public class RenderBlockFence extends RenderBlockCustomWood<TileEntityFence> {

	public RenderBlockFence() {
		super(AgriCraftBlocks.blockFence, new TileEntityFence(), true, false, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {

		drawScaledPrism(tess, 6, 0, 0, 10, 16, 4, matIcon);
		drawScaledPrism(tess, 6, 0, 12, 10, 16, 16, matIcon);
		drawScaledPrism(tess, 7, 12, 4, 9, 15, 12, matIcon);
		drawScaledPrism(tess, 7, 5, 4, 9, 8, 12, matIcon);

	}

	// Does this look cleaner?
	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFence) {
			TileEntityFence fence = (TileEntityFence) te;
			drawScaledPrism(tess, 6, 0, 6, 10, 16, 10, matIcon, cm);
			if (fence.canConnect(AgriForgeDirection.EAST)) {
				drawScaledPrism(tess, 10, 12, 7, 16, 15, 9, matIcon, cm);
				drawScaledPrism(tess, 10, 6, 7, 16, 9, 9, matIcon, cm);
			}
			if (fence.canConnect(AgriForgeDirection.WEST)) {
				drawScaledPrism(tess, 0, 12, 7, 6, 15, 9, matIcon, cm);
				drawScaledPrism(tess, 0, 6, 7, 6, 9, 9, matIcon, cm);
			}
			if (fence.canConnect(AgriForgeDirection.SOUTH)) {
				drawScaledPrism(tess, 7, 12, 10, 9, 15, 16, matIcon, cm);
				drawScaledPrism(tess, 7, 6, 10, 9, 9, 16, matIcon, cm);
			}
			if (fence.canConnect(AgriForgeDirection.NORTH)) {
				drawScaledPrism(tess, 7, 12, 0, 9, 15, 6, matIcon, cm);
				drawScaledPrism(tess, 7, 6, 0, 9, 9, 6, matIcon, cm);
			}
		}
	}

}
