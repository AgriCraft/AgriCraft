package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.Axis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {

	// Values to stop z-fighting.
	final static float A = 00.001f;
	final static float B = Constants.WHOLE - A;

	public RenderTank(BlockWaterTank block) {
		super(block, new TileEntityTank(), true, true, true);
	}

	@Override
	public void renderDynamicWood(ITessellator tess, TileEntityTank te, float partialTicks, int destroyStage, TextureAtlasSprite sprite) {
		drawWater(te, tess);
	}

	@Override
	public void renderStaticWood(ITessellator tess, TileEntityTank te, IBlockState state, TextureAtlasSprite sprite) {
		renderSide(tess, AgriForgeDirection.NORTH, state.getValue(AgriProperties.NORTH), sprite);
		renderSide(tess, AgriForgeDirection.EAST, state.getValue(AgriProperties.EAST), sprite);
		renderSide(tess, AgriForgeDirection.SOUTH, state.getValue(AgriProperties.SOUTH), sprite);
		renderSide(tess, AgriForgeDirection.WEST, state.getValue(AgriProperties.WEST), sprite);
		renderBottom(tess, state.getValue(AgriProperties.DOWN), sprite);
	}

	private void renderBottom(ITessellator tessellator, int code, TextureAtlasSprite icon) {
		if (code == 0) {
			tessellator.drawScaledPrism(A, 0, A, B, 1, B, icon);
		}
	}

	private void renderSide(ITessellator tessellator, AgriForgeDirection dir, int code, TextureAtlasSprite icon) {
		if (code != 3) {
			tessellator.pushMatrix();
			RenderUtil.rotateBlock(tessellator, dir);
			final float C = dir.axis == Axis.X ? 0 : A;
			final float D = Constants.WHOLE - C;
			if (code == 0) {
				tessellator.drawScaledPrism(A, C, 0, B, D, 2, icon);
			} else if (code == 1) {
				tessellator.drawScaledPrism(2, 0, 0, 14, 5, 2, icon);
				tessellator.drawScaledPrism(2, 5, 0, 5, 12, 2, icon);
				tessellator.drawScaledPrism(11, 5, 0, 14, 12, 2, icon);
				tessellator.drawScaledPrism(2, 12, 0, 14, 16, 2, icon);
			}
			tessellator.popMatrix();
		}
	}

	private void drawWater(TileEntityTank tank, ITessellator tessellator) {
		//only render water on the bottom layer
		if (tank.getYPosition() == 0) {
			//-0.0001F to avoid Z-fighting on maximum filled tanks
			float y = tank.getFluidHeight() - A;
			//the texture
			//stolen from Vanilla code
			/*
			int l = Blocks.water.colorMultiplier(tank.getWorld(), tank.getPos());
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorld(), tank.getPos()));
			tessellator.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			 */
			//draw surface
			final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
			tessellator.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, waterIcon, y);
		}
	}

}
