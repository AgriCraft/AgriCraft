package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockWaterTank;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
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

import com.infinityraider.agricraft.utility.icon.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {

	public RenderTank(BlockWaterTank block) {
		super(block, new TileEntityTank(), true, true, true);
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block, 
								 TileEntityTank tank, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite icon) {
		if(dynamicRender) {
			drawWater(tank, tessellator);			
		} else {
			drawWoodTank(tank, tessellator, icon);
		}
	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, TileEntityTank tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
		drawWoodTank(tile, tessellator, icon);
	}

	private void drawWoodTank(TileEntityTank tank, ITessellator tessellator, TextureAtlasSprite icon) {
		this.renderBottom(tank, tessellator, icon);
		this.renderSide(tank, tessellator, AgriForgeDirection.NORTH, icon);
		this.renderSide(tank, tessellator, AgriForgeDirection.EAST, icon);
		this.renderSide(tank, tessellator, AgriForgeDirection.SOUTH, icon);
		this.renderSide(tank, tessellator, AgriForgeDirection.WEST, icon);
	}

	private void renderBottom(TileEntityTank tank, ITessellator tessellator, TextureAtlasSprite icon) {
		//bottom
		boolean bottom = !tank.hasNeighbour(AgriForgeDirection.DOWN);
		if (bottom) {
			tessellator.drawScaledPrism(0, 0, 0, 16, 1, 16, icon);
		}
		//corners
		int yMin = bottom ? 1 : 0;
		if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
			tessellator.drawScaledPrism(0, yMin, 0, 2, 16, 2, icon);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
			tessellator.drawScaledPrism(14, yMin, 0, 16, 16, 2, icon);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
			tessellator.drawScaledPrism(0, yMin, 14, 2, 16, 16, icon);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
			tessellator.drawScaledPrism(14, yMin, 14, 16, 16, 16, icon);
		}
	}

	private void renderSide(TileEntityTank tank, ITessellator tessellator, AgriForgeDirection dir, TextureAtlasSprite icon) {
		int yMin = tank.hasNeighbour(AgriForgeDirection.DOWN) ? 0 : 1;
		if ((dir != null) && (dir != AgriForgeDirection.UNKNOWN)) {
			tessellator.pushMatrix();
			RenderUtil.rotateBlock(tessellator, dir);
			//connected to a channel
			if (tank.isConnectedToChannel(dir)) {
				tessellator.drawScaledPrism(2, yMin, 0, 14, 5, 2, icon);
				tessellator.drawScaledPrism(2, 5, 0, 5, 12, 2, icon);
				tessellator.drawScaledPrism(11, 5, 0, 14, 12, 2, icon);
				tessellator.drawScaledPrism(2, 12, 0, 14, 16, 2, icon);
			} //not connected to anything
			else if (!tank.hasNeighbour(dir)) {
				tessellator.drawScaledPrism(2, yMin, 0, 14, 16, 2, icon);
			}
			tessellator.popMatrix();
		}
	}

	private void drawWater(TileEntityTank tank, ITessellator tessellator) {
		//only render water on the bottom layer
		if (tank.getYPosition() == 0) {
			float y = tank.getFluidHeight() - 0.01F; //-0.0001F to avoid Z-fighting on maximum filled tanks
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
