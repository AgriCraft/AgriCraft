package com.infinityraider.agricraft.client.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.client.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelValve;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.client.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderChannelValve extends RenderChannel {

	public RenderChannelValve() {
		super(AgriCraftBlocks.blockChannelValve, new TileEntityChannelValve());
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {

		final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

		//Render channel.
		drawScaledPrism(tess, 2, 4, 4, 14, 12, 5, matIcon);
		drawScaledPrism(tess, 2, 4, 11, 14, 12, 12, matIcon);
		drawScaledPrism(tess, 2, 4, 5, 14, 5, 11, matIcon);

		//Render separators.
		drawScaledPrism(tess, 0.001f, 11.5f, 5, 1.999f, 15.001f, 11, sepIcon);
		drawScaledPrism(tess, 0.001f, 0.999f, 5, 1.999f, 5.5f, 11, sepIcon);
		drawScaledPrism(tess, 14.001f, 11.5f, 5, 15.999f, 15.001f, 11, sepIcon);
		drawScaledPrism(tess, 14.001f, 0.999f, 5, 15.999f, 5.5f, 11, sepIcon);

		//render the wooden guide rails along z-axis
		drawScaledPrism(tess, 0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(0, 0, 6 * Constants.UNIT);
		drawScaledPrism(tess, 0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(14 * Constants.UNIT, 0, 0);
		drawScaledPrism(tess, 0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(0, 0, -6 * Constants.UNIT);
		drawScaledPrism(tess, 0, 0, 3.999F, 2, 16, 5.999F, matIcon);
		tess.translate(-14 * Constants.UNIT, 0, 0);

	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityChannelValve) {
			// Get the Valve
			TileEntityChannelValve valve = (TileEntityChannelValve) te;

			// Render Base
			this.renderWoodChannel(tess, valve, matIcon, cm);

			// Get Separator Icon
			final TextureAtlasSprite sepIcon = BaseIcons.IRON_BLOCK.getIcon();

			// Draw Separators
			this.drawSeparators(tess, valve, matIcon, sepIcon, cm);
		}
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntityChannelValve) {
			TileEntityChannelValve valve = (TileEntityChannelValve) te;
			if (valve.getDiscreteFluidLevel() > 0) {
				renderCallCounter.incrementAndGet();
				this.drawWater(tess, valve);
			}
		}
	}

	protected void drawSeparators(TessellatorV2 tess, TileEntityChannelValve valve, TextureAtlasSprite matIcon, TextureAtlasSprite sepIcon, int cm) {
		for (AgriForgeDirection dir : TileEntityChannel.VALID_DIRECTIONS) {
			if (valve.hasNeighbourCheck(dir)) {
				if (valve.isPowered()) {
					//Draw closed separator.
					drawScaledPrism(tess, 6, 5, 0, 10, 12, 2, sepIcon, cm, dir);
				} else {
					//Draw open separator.
					drawScaledPrism(tess, 6, 1, 0, 10, 5.001F, 2, sepIcon, cm, dir);
					drawScaledPrism(tess, 6, 12, 0, 10, 15, 2, sepIcon, cm, dir);
				}
				//Draw rails.
				drawScaledPrism(tess, 4, 0, 0, 6, 16, 2, matIcon, cm, dir);
				drawScaledPrism(tess, 10, 0, 0, 12, 16, 2, matIcon, cm, dir);
			}
		}
	}

	@Override
	protected void renderSide(TessellatorV2 tess, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm, AgriForgeDirection direction) {
		if (channel.getWorld() != null) {
			IBlockState neighbour = channel.getWorld().getBlockState(channel.getPos().add(direction.offsetX, 0, direction.offsetZ));
			if (neighbour != null) {
				if (neighbour instanceof BlockLever && neighbour.getValue(BlockLever.FACING).getFacing() == direction.getEnumFacing()) {
					drawScaledPrism(tess, 5, 4, 0, 11, 12, 4, matIcon, cm, direction);
				}
			}
		}
		super.renderSide(tess, channel, matIcon, cm, direction);
	}

}
