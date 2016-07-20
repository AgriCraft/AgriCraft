package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.pad.AbstractBlockWaterPad;
import com.infinityraider.agricraft.blocks.pad.BlockWaterPad;
import com.infinityraider.agricraft.blocks.pad.BlockWaterPadFull;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.TileEntityBase;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockBase<TileEntityBase> {

	public RenderWaterPad(AbstractBlockWaterPad block) {
		super(block, null, true, false, true);
	}

	@Override
	public void renderStatic(ITessellator tess, TileEntityBase te, IBlockState state) {
		// Check Full
		final boolean full = te.getBlockType() instanceof BlockWaterPadFull;
		// Get World
		final World world = te.getWorld();
		// Get Pos
		final BlockPos pos = te.getPos();
		// Render
		this.renderBase(tess, world, pos, full);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.NORTH);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.EAST);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.SOUTH);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.WEST);
	}

	@Override
	public void renderInventoryBlock(ITessellator tess, World world, IBlockState state, Block block, @Nullable TileEntityBase tile,
									 ItemStack stack, EntityLivingBase entity) {

		// Icon
		final TextureAtlasSprite dirtIcon = BaseIcons.DIRT.getIcon();

		// Draw
		tess.drawScaledPrism(0, 0, 0, 16, 8, 16, dirtIcon);
		tess.drawScaledPrism(1, 8, 0, 1, 15, 16, dirtIcon);
		tess.drawScaledPrism(15, 8, 1, 16, 15, 16, dirtIcon);
		tess.drawScaledPrism(0, 8, 15, 15, 15, 16, dirtIcon);
		tess.drawScaledPrism(0, 8, 0, 15, 1, 15, dirtIcon);

		// Full
		if (((ItemBlock) stack.getItem()).block instanceof BlockWaterPadFull) {
			TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
		}
	}

	private void renderBase(ITessellator tess, IBlockAccess world, BlockPos pos, boolean full) {		
		// Get Icon
		final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();

		//tess.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
		tess.setColorRGBA(1, 1, 1, 1);
		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.WEST, AgriForgeDirection.NORTH)) {
			//renderer.setRenderBounds(0, 8 * u, 0, u, 15 * u, 1 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			/*
			int l = Blocks.water.colorMultiplier(world, pos);
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
			tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			*/
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(0, 0, 1, 1, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.NORTH, AgriForgeDirection.EAST)) {
			//renderer.setRenderBounds(15 * u, 8 * u, 0, 16 * u, 15 * u, 1 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			/*
			int l = Blocks.water.colorMultiplier(world, pos);
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
			tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			*/
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(15, 0, 16, 1, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.EAST, AgriForgeDirection.SOUTH)) {
			//renderer.setRenderBounds(15 * u, 8 * u, 15 * u, 16 * u, 15 * u, 16 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			/*
			int l = Blocks.water.colorMultiplier(world, pos);
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
			tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			*/
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(15, 15, 16, 16, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.SOUTH, AgriForgeDirection.WEST)) {
			//renderer.setRenderBounds(0, 8 * u, 15 * u, u, 15 * u, 16 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			/*
			int l = Blocks.water.colorMultiplier(world, pos);
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
			tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			*/
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(0, 15, 1, 16, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}
		//renderer.renderAllFaces = renderAllFaces;
		if (full) {
			/*
			int l = Blocks.water.colorMultiplier(world, pos);
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
			tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
			*/
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(1, 1, 15, 15, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}
	}

	private boolean shouldRenderCorner(IBlockAccess world, BlockPos pos, boolean full, AgriForgeDirection dir1, AgriForgeDirection dir2) {
		Block block = world.getBlockState(pos.add(dir1.offsetX, 0, dir1.offsetZ)).getBlock();
		boolean flag1 = block instanceof BlockWaterPad;
		boolean flag2 = block instanceof BlockWaterPadFull;
		if (!flag1 || (full != flag2)) {
			return true;
		}
		block = world.getBlockState(pos.add(dir2.offsetX, 0, dir2.offsetZ)).getBlock();
		flag1 = block instanceof BlockWaterPad;
		flag2 = block instanceof BlockWaterPadFull;
		if (!flag1 || (full != flag2)) {
			return true;
		}
		block = world.getBlockState(pos.add(dir1.offsetX + dir2.offsetX, 0, dir1.offsetZ + dir2.offsetZ)).getBlock();
		flag1 = block instanceof BlockWaterPad;
		flag2 = block instanceof BlockWaterPadFull;
		return !flag1 || (full != flag2);
	}

	private void renderSide(ITessellator tess, IBlockAccess world, BlockPos pos, boolean full, AgriForgeDirection side) {
		int xLower = Math.max(0, 1 + 14 * side.offsetX);
		int xUpper = Math.min(16, 15 + 14 * side.offsetX);
		int zLower = Math.max(0, 1 + 14 * side.offsetZ);
		int zUpper = Math.min(16, 15 + 14 * side.offsetZ);
		Block block = world.getBlockState(pos.add(side.offsetX, 0, side.offsetZ)).getBlock();
		if (block != null && block instanceof BlockWaterPad) {
			boolean flag = block instanceof BlockWaterPadFull;
			if (full) {
				TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO: get water icon
				/*
				int l = Blocks.water.colorMultiplier(world, pos);
				float f = (float) (l >> 16 & 255) / 255.0F;
				float f1 = (float) (l >> 8 & 255) / 255.0F;
				float f2 = (float) (l & 255) / 255.0F;
				float f4 = 1.0F;
				tess.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
				tess.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);
				tess.translate(pos.getX(), pos.getY(), pos.getZ());
				*/

				tess.drawScaledFace(xLower, zLower, xUpper, zUpper, EnumFacing.UP, icon, 14);
				tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			}
			if (flag == full) {
				return;
			}
		}
		//tess.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
		tess.setColorRGBA(1, 1, 1, 1);
		//boolean renderAllFaces = renderer.renderAllFaces;
		//renderer.renderAllFaces = true;
		//renderer.setRenderBounds(xLower * u, 8 * u, zLower * u, xUpper * u, 15 * u, zUpper * u);
		//renderer.renderStandardBlock(Blocks.farmland, pos);
		//renderer.renderAllFaces = renderAllFaces;
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return null;
	}
}
