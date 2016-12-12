package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.pad.AbstractBlockWaterPad;
import com.infinityraider.agricraft.blocks.pad.BlockWaterPadFull;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.infinitylib.block.blockstate.SidedConnection;
import com.infinityraider.infinitylib.render.block.RenderBlockBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockBase<AbstractBlockWaterPad> {
    private static final SidedConnection DEFAULT = new SidedConnection();

	public RenderWaterPad(AbstractBlockWaterPad block) {
		super(block, true);
	}

	@Override
	public List<ResourceLocation> getAllTextures() {
		return Collections.emptyList();
	}

	@Override
	public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, AbstractBlockWaterPad block, EnumFacing side) {
		// Check Full
		final boolean full = block instanceof BlockWaterPadFull;
		SidedConnection connection = state instanceof IExtendedBlockState ? ((IExtendedBlockState) state).getValue(AgriProperties.CONNECTIONS) : DEFAULT;
		// Render
		this.renderBase(tessellator, connection, full);
		this.renderSide(tessellator, connection, full, EnumFacing.NORTH);
		this.renderSide(tessellator, connection, full, EnumFacing.EAST);
		this.renderSide(tessellator, connection, full, EnumFacing.SOUTH);
		this.renderSide(tessellator, connection, full, EnumFacing.WEST);
	}

	@Override
	public void renderInventoryBlock(ITessellator tess, World world, IBlockState state, AbstractBlockWaterPad block,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

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

    //TODO: render water
	private void renderBase(ITessellator tess, SidedConnection connection, boolean full) {
		// Get Icon
		final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();

		//tess.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
		tess.setColorRGBA(1, 1, 1, 1);
		if (shouldRenderCorner(connection, full, EnumFacing.WEST, EnumFacing.NORTH)) {
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
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(0, 0, 1, 1, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			*/
		}

		if (shouldRenderCorner(connection, full, EnumFacing.NORTH, EnumFacing.EAST)) {
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
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(15, 0, 16, 1, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			*/
		}

		if (shouldRenderCorner(connection, full, EnumFacing.EAST, EnumFacing.SOUTH)) {
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
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(15, 15, 16, 16, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			*/
		}

		if (shouldRenderCorner(connection, full, EnumFacing.SOUTH, EnumFacing.WEST)) {
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
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(0, 15, 1, 16, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			*/
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
			tess.translate(pos.getX(), pos.getY(), pos.getZ());
			tess.drawScaledFace(1, 1, 15, 15, EnumFacing.UP, waterIcon, 14);
			tess.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			*/
		}
	}

	private boolean shouldRenderCorner(SidedConnection connection, boolean full, EnumFacing dir1, EnumFacing dir2) {
        return connection.isConnected(dir1) && connection.isConnected(dir2);
	}

	private void renderSide(ITessellator tess, SidedConnection connection, boolean full, EnumFacing side) {
		int xLower = Math.max(0, 1 + 14 * side.getFrontOffsetX());
		int xUpper = Math.min(16, 15 + 14 * side.getFrontOffsetX());
		int zLower = Math.max(0, 1 + 14 * side.getFrontOffsetZ());
		int zUpper = Math.min(16, 15 + 14 * side.getFrontOffsetZ());
        if(connection.isConnected(side)) {
            if(full) {
                //TODO: render water
            }
        } else {
            //TODO: render side
        }
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return null;
	}

	@Override
	public boolean applyAmbientOcclusion() {
		return true;
	}
}