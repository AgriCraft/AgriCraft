package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelValve;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelValve;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderChannelValve extends RenderChannel<BlockWaterChannelValve, TileEntityChannelValve> {

	public RenderChannelValve(BlockWaterChannelValve block) {
		super(block, new TileEntityChannelValve());
	}

	@Override
	public void renderInventoryBlockWood(ITessellator tessellator, World world, IBlockState state, BlockWaterChannelValve block, TileEntityChannelValve tile,
										 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {

		final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

		//Render channel.
		tessellator.drawScaledPrism(2, 4, 4, 14, 12, 5, icon);
		tessellator.drawScaledPrism(2, 4, 11, 14, 12, 12, icon);
		tessellator.drawScaledPrism(2, 4, 5, 14, 5, 11, icon);

		//Render separators.
		tessellator.drawScaledPrism(0.001f, 11.5f, 5, 1.999f, 15.001f, 11, sepIcon);
		tessellator.drawScaledPrism(0.001f, 0.999f, 5, 1.999f, 5.5f, 11, sepIcon);
		tessellator.drawScaledPrism(14.001f, 11.5f, 5, 15.999f, 15.001f, 11, sepIcon);
		tessellator.drawScaledPrism(14.001f, 0.999f, 5, 15.999f, 5.5f, 11, sepIcon);

		//render the wooden guide rails along z-axis
		tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
		tessellator.translate(0, 0, 6 * Constants.UNIT);
		tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
		tessellator.translate(14 * Constants.UNIT, 0, 0);
		tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
		tessellator.translate(0, 0, -6 * Constants.UNIT);
		tessellator.drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, icon);
		tessellator.translate(-14 * Constants.UNIT, 0, 0);
	}

	@Override
	protected void renderSideRotated(ITessellator tessellator, TileEntityChannelValve channel, EnumFacing dir, boolean hasNeighbour, TextureAtlasSprite matIcon) {
		if (channel.getWorld() != null) {
			IBlockState neighbour = channel.getWorld().getBlockState(channel.getPos().add(dir.getFrontOffsetX(), 0, dir.getFrontOffsetZ()));
			if (neighbour != null) {
				if (neighbour instanceof BlockLever && neighbour.getValue(BlockLever.FACING).getFacing() == dir) {
					tessellator.drawScaledPrism(5, 4, 0, 11, 12, 4, matIcon);
				}
			}
		}
		if (!hasNeighbour) {
			renderSeparator(tessellator, channel, matIcon, BaseIcons.IRON_BLOCK.getIcon());
		}
		super.renderSideRotated(tessellator, channel, dir, hasNeighbour, matIcon);
	}

	protected void renderSeparator(ITessellator tessellator, TileEntityChannelValve valve, TextureAtlasSprite matIcon, TextureAtlasSprite sepIcon) {
		if (valve.isPowered()) {
			//Draw closed separator.
			tessellator.drawScaledPrism(6, 5, 0, 10, 12, 2, sepIcon);
		} else {
			//Draw open separator.
			tessellator.drawScaledPrism(6, 1, 0, 10, 5.001F, 2, sepIcon);
			tessellator.drawScaledPrism(6, 12, 0, 10, 15, 2, sepIcon);
		}
		//Draw rails.
		tessellator.drawScaledPrism(3, 0, 0, 6, 16, 2, matIcon);
		tessellator.drawScaledPrism(10, 0, 0, 13, 16, 2, matIcon);
	}

}
