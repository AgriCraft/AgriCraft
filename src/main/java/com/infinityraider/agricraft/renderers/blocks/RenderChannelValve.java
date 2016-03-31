package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelValve;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.icon.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderChannelValve extends RenderChannel<TileEntityChannelValve> {

	public RenderChannelValve() {
		super(AgriCraftBlocks.blockChannelValve, new TileEntityChannelValve());
	}

	/*
	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {

		final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

		//Render channel.
		drawScaledPrism(2, 4, 4, 14, 12, 5, matIcon);
		drawScaledPrism(2, 4, 11, 14, 12, 12, matIcon);
		drawScaledPrism(2, 4, 5, 14, 5, 11, matIcon);

		//Render separators.
		drawScaledPrism(0.001f, 11.5f, 5, 1.999f, 15.001f, 11, sepIcon);
		drawScaledPrism(0.001f, 0.999f, 5, 1.999f, 5.5f, 11, sepIcon);
		drawScaledPrism(14.001f, 11.5f, 5, 15.999f, 15.001f, 11, sepIcon);
		drawScaledPrism(14.001f, 0.999f, 5, 15.999f, 5.5f, 11, sepIcon);

		//render the wooden guide rails along z-axis
		drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(0, 0, 6 * Constants.UNIT);
		drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(14 * Constants.UNIT, 0, 0);
		drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(0, 0, -6 * Constants.UNIT);
		drawScaledPrism(0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(-14 * Constants.UNIT, 0, 0);

	}
	*/

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
								 TileEntityChannelValve valve, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite matIcon) {
		if(dynamicRender) {
			this.drawWater(tessellator, valve);
		} else {
			// Render Base
			this.renderWoodChannel(tessellator, valve, matIcon);

			// Get Separator Icon
			final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

			// Draw Separators
			this.drawSeparators(tessellator, valve, matIcon, sepIcon);
		}
	}

	protected void drawSeparators(ITessellator tessellator, TileEntityChannelValve valve, TextureAtlasSprite matIcon, TextureAtlasSprite sepIcon) {
		for (AgriForgeDirection dir : TileEntityChannel.VALID_DIRECTIONS) {
			if (valve.hasNeighbourCheck(dir)) {
				if (valve.isPowered()) {
					//Draw closed separator.
					tessellator.drawScaledPrism(6, 5, 0, 10, 12, 2, sepIcon);
				} else {
					//Draw open separator.
					tessellator.drawScaledPrism(6, 1, 0, 10, 5.001F, 2, sepIcon);
					tessellator.drawScaledPrism(6, 12, 0, 10, 15, 2, sepIcon);
				}
				//Draw rails.
				tessellator.drawScaledPrism(4, 0, 0, 6, 16, 2, matIcon);
				tessellator.drawScaledPrism(10, 0, 0, 12, 16, 2, matIcon);
			}
		}
	}

	@Override
	protected void renderSide(ITessellator tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, AgriForgeDirection direction) {
		if (channel.getWorld() != null) {
			IBlockState neighbour = channel.getWorld().getBlockState(channel.getPos().add(direction.offsetX, 0, direction.offsetZ));
			if (neighbour != null) {
				if (neighbour instanceof BlockLever && neighbour.getValue(BlockLever.FACING).getFacing() == direction.getEnumFacing()) {
					tessellator.drawScaledPrism(5, 4, 0, 11, 12, 4, matIcon);
				}
			}
		}
		super.renderSide(tessellator, channel, matIcon, direction);
	}

}
