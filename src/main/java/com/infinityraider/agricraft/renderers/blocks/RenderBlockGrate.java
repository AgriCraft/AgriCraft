package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import com.infinityraider.agricraft.utility.icon.BaseIcons;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderBlockGrate extends RenderBlockCustomWood<TileEntityGrate> {
	public RenderBlockGrate() {
		super(AgriCraftBlocks.blockGrate, new TileEntityGrate(), true, false, true);
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
								 @Nullable TileEntityGrate grate, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite matIcon) {
		// Setup
		final float offset = ((float) grate.getOffset() * 7) / 16.0F;

		// Offset
		tessellator.translate(0, 0, offset);

		// Draw Grate
		tessellator.drawScaledPrism(1, 0, 0, 3, 16, 2, matIcon);
		tessellator.drawScaledPrism(5, 0, 0, 7, 16, 2, matIcon);
		tessellator.drawScaledPrism(9, 0, 0, 11, 16, 2, matIcon);
		tessellator.drawScaledPrism(13, 0, 0, 15, 16, 2, matIcon);
		tessellator.drawScaledPrism(0, 1, 0, 16, 3, 2, matIcon);
		tessellator.drawScaledPrism(0, 5, 0, 16, 7, 2, matIcon);
		tessellator.drawScaledPrism(0, 9, 0, 16, 11, 2, matIcon);
		tessellator.drawScaledPrism(0, 13, 0, 16, 15, 2, matIcon);

		//vines
		final TextureAtlasSprite vinesIcon = BaseIcons.VINE.getIcon();
		int l = RenderUtil.getMixedBrightness(world, pos, Blocks.vine.getDefaultState());
		float f0 = (float) (l >> 16 & 255) / 255.0F;
		float f1 = (float) (l >> 8 & 255) / 255.0F;
		float f2 = (float) (l & 255) / 255.0F;
		tessellator.setColorRGB(f0, f1, f2);

		if (grate.hasVines(true)) {
			tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, vinesIcon, 0.001f);
		}
		if (grate.hasVines(false)) {
			tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, vinesIcon, 1.999f);
		}
	}

	/*
	@Override
	protected void doInventoryRender(TessellatorV2 ItemStack item, TextureAtlasSprite matIcon) {
		drawScaledPrism(7, 0, 1, 9, 16, 3, matIcon);
		drawScaledPrism(7, 0, 5, 9, 16, 7, matIcon);
		drawScaledPrism(7, 0, 9, 9, 16, 11, matIcon);
		drawScaledPrism(7, 0, 13, 9, 16, 15, matIcon);
		drawScaledPrism(7, 1, 0, 9, 3, 16, matIcon);
		drawScaledPrism(7, 5, 0, 9, 7, 16, matIcon);
		drawScaledPrism(7, 9, 0, 9, 11, 16, matIcon);
		drawScaledPrism(7, 13, 0, 9, 15, 16, matIcon);
	}
	*/
}
