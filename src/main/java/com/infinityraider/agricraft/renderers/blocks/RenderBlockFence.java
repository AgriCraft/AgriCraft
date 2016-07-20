package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.decoration.BlockFence;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.decoration.TileEntityFence;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RenderBlockFence extends RenderBlockCustomWood<TileEntityFence> {

	public RenderBlockFence(BlockFence block) {
		super(block, new TileEntityFence(), true, false, true);
	}

	@Override
	protected void renderStaticWood(ITessellator tess, TileEntityFence fence, IBlockState state, TextureAtlasSprite sprite) {
		tess.drawScaledPrism(6, 0, 6, 10, 16, 10, sprite);
		if (fence.canConnect(AgriForgeDirection.EAST)) {
			tess.drawScaledPrism(10, 12, 7, 16, 15, 9, sprite);
			tess.drawScaledPrism(10, 6, 7, 16, 9, 9, sprite);
		}
		if (fence.canConnect(AgriForgeDirection.WEST)) {
			tess.drawScaledPrism(0, 12, 7, 6, 15, 9, sprite);
			tess.drawScaledPrism(0, 6, 7, 6, 9, 9, sprite);
		}
		if (fence.canConnect(AgriForgeDirection.SOUTH)) {
			tess.drawScaledPrism(7, 12, 10, 9, 15, 16, sprite);
			tess.drawScaledPrism(7, 6, 10, 9, 9, 16, sprite);
		}
		if (fence.canConnect(AgriForgeDirection.NORTH)) {
			tess.drawScaledPrism(7, 12, 0, 9, 15, 6, sprite);
			tess.drawScaledPrism(7, 6, 0, 9, 9, 6, sprite);
		}
	}

	@Override
	public void renderInventoryBlockWood(ITessellator tess, World world, IBlockState state, Block block, TileEntityFence tile,
									 ItemStack stack, EntityLivingBase entity, TextureAtlasSprite matIcon) {
		tess.drawScaledPrism(6, 0, 0, 10, 16, 4, matIcon);
		tess.drawScaledPrism(6, 0, 12, 10, 16, 16, matIcon);
		tess.drawScaledPrism(7, 12, 4, 9, 15, 12, matIcon);
		tess.drawScaledPrism(7, 5, 4, 9, 8, 12, matIcon);
	}
}
