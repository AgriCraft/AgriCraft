package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityTank;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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

	private void renderBottom(ITessellator tessellator, TextureAtlasSprite icon) {
		tessellator.drawScaledFace(A, A, B, B, EnumFacing.DOWN, icon, 0);
		tessellator.drawScaledFace(A, A, B, B, EnumFacing.UP, icon, 1);
	}

	private void renderSide(ITessellator tessellator, EnumFacing dir, TileEntityTank.Connection connection, TextureAtlasSprite icon) {
		if (connection == TileEntityTank.Connection.TANK) {
			return;
		}
		//data about side to render
		boolean xAxis = dir.getAxis() == EnumFacing.Axis.X;
		int index = xAxis ? dir.getFrontOffsetX() : dir.getFrontOffsetZ();
		int min = index < 0 ? 0 : 14;
		int max = index < 0 ? 2 : 16;

		//render upper face
		tessellator.drawScaledFace(xAxis ? min : 0, xAxis ? 0 : min, xAxis ? max : 16, xAxis ? 16 : max, EnumFacing.UP, icon, 16);

		//render side
		if (connection == TileEntityTank.Connection.NONE) {
			tessellator.drawScaledFace(0, 0, 16, 16, dir, icon, index > 0 ? 16 : 0);
			tessellator.drawScaledFace(0, 0, 16, 16, dir.getOpposite(), icon, index > 0 ? 14 : 2);
		} else {
			//vertical faces

			//lower part, under the channel
			tessellator.drawScaledFace(0, 0, 16, 5, dir, icon, index > 0 ? 16 : 0);
			tessellator.drawScaledFace(0, 0, 16, 5, dir.getOpposite(), icon, index > 0 ? 14 : 2);
			//left center part, same height as the channel
			tessellator.drawScaledFace(0, 5, 5, 12, dir, icon, index > 0 ? 16 : 0);
			tessellator.drawScaledFace(0, 5, 5, 12, dir.getOpposite(), icon, index > 0 ? 14 : 2);
			//right center part, same height as the channel
			tessellator.drawScaledFace(11, 5, 16, 12, dir, icon, index > 0 ? 16 : 0);
			tessellator.drawScaledFace(11, 5, 16, 12, dir.getOpposite(), icon, index > 0 ? 14 : 2);
			//upper part, above the channel
			tessellator.drawScaledFace(0, 12, 16, 16, dir, icon, index > 0 ? 16 : 0);
			tessellator.drawScaledFace(0, 12, 16, 16, dir.getOpposite(), icon, index > 0 ? 14 : 2);

			//inside of the gap
			tessellator.drawScaledFace(xAxis ? min : 5, xAxis ? 5 : min, xAxis ? max : 11, xAxis ? 11 : max, EnumFacing.UP, icon, 5);
			tessellator.drawScaledFace(xAxis ? min : 5, xAxis ? 5 : min, xAxis ? max : 11, xAxis ? 11 : max, EnumFacing.DOWN, icon, 12);

			EnumFacing left = xAxis ? EnumFacing.NORTH : EnumFacing.WEST;
			EnumFacing right = left.getOpposite();

			tessellator.drawScaledFace(min, 5, max, 12, left, icon, 11);
			tessellator.drawScaledFace(min, 5, max, 12, right, icon, 5);
		}
	}

	@Override
	protected void renderStaticWood(ITessellator tess, TileEntityTank tile, TextureAtlasSprite sprite) {
		TileEntityTank.Connection north = tile.getConnectionType(EnumFacing.NORTH);
		TileEntityTank.Connection east = tile.getConnectionType(EnumFacing.EAST);
		TileEntityTank.Connection south = tile.getConnectionType(EnumFacing.SOUTH);
		TileEntityTank.Connection west = tile.getConnectionType(EnumFacing.WEST);
		renderSide(tess, EnumFacing.NORTH, north, sprite);
		renderSide(tess, EnumFacing.EAST, east, sprite);
		renderSide(tess, EnumFacing.SOUTH, south, sprite);
		renderSide(tess, EnumFacing.WEST, west, sprite);
		if (!tile.hasNeighbour(EnumFacing.DOWN)) {
			renderBottom(tess, sprite);
		}
	}

	@Override
	public void renderDynamicTile(ITessellator tess, TileEntityTank tank, float partialTicks, int destroyStage) {
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
			tess.drawScaledFace(0, 0, 16, 16, EnumFacing.UP, waterIcon, y);
		}
	}

	@Override
	public void renderInventoryBlockWood(ITessellator tess, World world, TileEntityTank tile, ItemStack stack, EntityLivingBase entity, TextureAtlasSprite sprite) {
		renderSide(tess, EnumFacing.NORTH, TileEntityTank.Connection.NONE, sprite);
		renderSide(tess, EnumFacing.EAST, TileEntityTank.Connection.NONE, sprite);
		renderSide(tess, EnumFacing.SOUTH, TileEntityTank.Connection.NONE, sprite);
		renderSide(tess, EnumFacing.WEST, TileEntityTank.Connection.NONE, sprite);
		renderBottom(tess, sprite);
	}

	@Override
	public boolean applyAmbientOcclusion() {
		return true;
	}

}
