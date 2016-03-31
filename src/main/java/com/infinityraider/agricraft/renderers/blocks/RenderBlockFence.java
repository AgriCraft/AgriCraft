package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityFence;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderBlockFence extends RenderBlockCustomWood<TileEntityFence> {

	public RenderBlockFence() {
		super(AgriCraftBlocks.blockFence, new TileEntityFence(), true, false, true);
	}

	/*
	@Override
	protected void doInventoryRender(ITessellator tess, ItemStack item, TextureAtlasSprite matIcon) {
		drawScaledPrism(tess, 6, 0, 0, 10, 16, 4, matIcon);
		drawScaledPrism(tess, 6, 0, 12, 10, 16, 16, matIcon);
		drawScaledPrism(tess, 7, 12, 4, 9, 15, 12, matIcon);
		drawScaledPrism(tess, 7, 5, 4, 9, 8, 12, matIcon);

	}
	*/

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
								 TileEntityFence fence, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite matIcon) {
		tessellator.drawScaledPrism(6, 0, 6, 10, 16, 10, matIcon);
		if (fence.canConnect(AgriForgeDirection.EAST)) {
			tessellator.drawScaledPrism(10, 12, 7, 16, 15, 9, matIcon);
			tessellator.drawScaledPrism(10, 6, 7, 16, 9, 9, matIcon);
		}
		if (fence.canConnect(AgriForgeDirection.WEST)) {
			tessellator.drawScaledPrism(0, 12, 7, 6, 15, 9, matIcon);
			tessellator.drawScaledPrism(0, 6, 7, 6, 9, 9, matIcon);
		}
		if (fence.canConnect(AgriForgeDirection.SOUTH)) {
			tessellator.drawScaledPrism(7, 12, 10, 9, 15, 16, matIcon);
			tessellator.drawScaledPrism(7, 6, 10, 9, 9, 16, matIcon);
		}
		if (fence.canConnect(AgriForgeDirection.NORTH)) {
			tessellator.drawScaledPrism(7, 12, 0, 9, 15, 6, matIcon);
			tessellator.drawScaledPrism(7, 6, 0, 9, 9, 6, matIcon);
		}
	}
}
