package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockFenceGate;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.decoration.TileEntityFenceGate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderBlockFenceGate extends RenderBlockCustomWood<TileEntityFenceGate> {

	public RenderBlockFenceGate(BlockFenceGate block) {
		super(block, new TileEntityFenceGate(), true, false, true);
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, IBlockState state, Block block,
								 TileEntityFenceGate gate, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite icon) {
		if (gate.isZAxis()) {
			renderGateZ(tessellator, gate, icon);
		} else {
			renderGateX(tessellator, gate, icon);
		}
	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, TileEntityFenceGate tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
		tessellator.drawScaledPrism(7, 5, 0, 9, 16, 2, icon);
		tessellator.drawScaledPrism(7, 5, 14, 9, 16, 16, icon);
		tessellator.drawScaledPrism(7, 12, 2, 9, 15, 14, icon);
		tessellator.drawScaledPrism(7, 6, 2, 9, 9, 14, icon);
		tessellator.drawScaledPrism(7, 9, 6, 9, 12, 10, icon);
	}

	private void renderGateX(ITessellator tessellator, TileEntityFenceGate gate, TextureAtlasSprite icon) {
		tessellator.drawScaledPrism(7, 5, 0, 9, 16, 2, icon);
		tessellator.drawScaledPrism(7, 5, 14, 9, 16, 16, icon);
		if (!gate.isOpen()) {
			tessellator.drawScaledPrism(7, 12, 2, 9, 15, 14, icon);
			tessellator.drawScaledPrism(7, 6, 2, 9, 9, 14, icon);
			tessellator.drawScaledPrism(7, 9, 6, 9, 12, 10, icon);
		} else if (gate.getOpenDirection() > 0) {
			tessellator.drawScaledPrism(1, 12, 0, 7, 15, 2, icon);
			tessellator.drawScaledPrism(1, 12, 14, 7, 15, 16, icon);
			tessellator.drawScaledPrism(1, 6, 0, 7, 9, 2, icon);
			tessellator.drawScaledPrism(1, 6, 14, 7, 9, 16, icon);
			tessellator.drawScaledPrism(1, 9, 0, 3, 12, 2, icon);
			tessellator.drawScaledPrism(1, 9, 14, 3, 12, 16, icon);
		} else {
			tessellator.drawScaledPrism(9, 12, 0, 15, 15, 2, icon);
			tessellator.drawScaledPrism(9, 12, 14, 15, 15, 16, icon);
			tessellator.drawScaledPrism(9, 6, 0, 15, 9, 2, icon);
			tessellator.drawScaledPrism(9, 6, 14, 15, 9, 16, icon);
			tessellator.drawScaledPrism(13, 9, 0, 15, 12, 2, icon);
			tessellator.drawScaledPrism(13, 9, 14, 15, 12, 16, icon);
		}
	}

	private void renderGateZ(ITessellator tessellator, TileEntityFenceGate gate, TextureAtlasSprite icon) {
		tessellator.drawScaledPrism(0, 5, 7, 2, 16, 9, icon);
		tessellator.drawScaledPrism(14, 5, 7, 16, 16, 9, icon);
		if (!gate.isOpen()) {
			tessellator.drawScaledPrism(2, 12, 7, 14, 15, 9, icon);
			tessellator.drawScaledPrism(2, 6, 7, 14, 9, 9, icon);
			tessellator.drawScaledPrism(6, 9, 7, 10, 12, 9, icon);
		} else if (gate.getOpenDirection() > 0) {
			tessellator.drawScaledPrism(0, 12, 1, 2, 15, 7, icon);
			tessellator.drawScaledPrism(14, 12, 1, 16, 15, 7, icon);
			tessellator.drawScaledPrism(0, 6, 1, 2, 9, 7, icon);
			tessellator.drawScaledPrism(14, 6, 1, 16, 9, 7, icon);
			tessellator.drawScaledPrism(0, 9, 1, 2, 12, 3, icon);
			tessellator.drawScaledPrism(14, 9, 1, 16, 12, 3, icon);
		} else {
			tessellator.drawScaledPrism(0, 12, 9, 2, 15, 15, icon);
			tessellator.drawScaledPrism(14, 12, 9, 16, 15, 15, icon);
			tessellator.drawScaledPrism(0, 6, 9, 2, 9, 15, icon);
			tessellator.drawScaledPrism(14, 6, 9, 16, 9, 15, icon);
			tessellator.drawScaledPrism(0, 9, 13, 2, 12, 15, icon);
			tessellator.drawScaledPrism(14, 9, 13, 16, 12, 15, icon);
		}
	}
}
