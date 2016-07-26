package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityTank;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<BlockWaterTank, TileEntityTank> {

	// Values to stop z-fighting.
	final static float A = 00.001f;
	final static float B = Constants.WHOLE - A;

	public RenderTank(BlockWaterTank block) {
		super(block, new TileEntityTank(), true, true, true);
	}

	private void renderBottom(ITessellator tessellator, int code, TextureAtlasSprite icon) {
		if (code == 0) {
			tessellator.drawScaledPrism(A, 0, A, B, 1, B, icon);
		}
	}

	private void renderSide(ITessellator tessellator, EnumFacing dir, int code, TextureAtlasSprite icon) {
		if (code != 3) {
			tessellator.pushMatrix();
			this.rotateBlock(tessellator, dir);
			final float C = dir.getAxis() == EnumFacing.Axis.X ? 0 : A;
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

	@Override
	protected void renderWorldBlockWood(ITessellator tess, World world, BlockPos pos, IBlockState state, BlockWaterTank block,
										TileEntityTank tile, TextureAtlasSprite sprite, boolean dynamic) {
		if(dynamic) {
			drawWater(tile, tess);
		} else {
            //TODO: figure out what these code parameters do
			renderSide(tess, EnumFacing.NORTH, 0, sprite);
			renderSide(tess, EnumFacing.EAST, 0, sprite);
			renderSide(tess, EnumFacing.SOUTH, 0, sprite);
			renderSide(tess, EnumFacing.WEST, 0, sprite);
			renderBottom(tess, 0, sprite);
		}

	}

	@Override
	protected void renderInventoryBlockWood(ITessellator tess, World world, IBlockState state, BlockWaterTank block, TileEntityTank tile,
											ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {

	}
}
