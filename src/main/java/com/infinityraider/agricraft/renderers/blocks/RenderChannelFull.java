package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel<BlockWaterChannelFull, TileEntityChannelFull> {

	public RenderChannelFull(BlockWaterChannelFull block) {
		super(block, new TileEntityChannelFull());
	}

	@Override
	protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
		//draw bottom
		tessellator.drawScaledPrism(0, 0, 0, 16, 5, 16, matIcon);
		//draw top
		tessellator.drawScaledPrism(0, 12, 0, 16, 16, 16, matIcon);
		//draw four corners
		tessellator.drawScaledPrism(0, 5, 0, 5, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 0, 16, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 11, 16, 12, 16, matIcon);
		tessellator.drawScaledPrism(0, 5, 11, 5, 12, 16, matIcon);

	}

	@Override
	protected void renderSideRotated(ITessellator tessellator, TileEntityChannelFull channel, EnumFacing dir, boolean hasNeighbour, TextureAtlasSprite matIcon) {
		if (!hasNeighbour) {
			tessellator.drawScaledPrism(5, 5, 0, 11, 12, 5, matIcon);
		}
	}

	@Override
	protected void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, BlockWaterChannelFull block, TileEntityChannelFull channel,
											ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
		this.renderBottom(tessellator, icon);
		this.renderSide(tessellator, channel, EnumFacing.NORTH, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.EAST, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.SOUTH, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.WEST, true, icon);

	}
}
