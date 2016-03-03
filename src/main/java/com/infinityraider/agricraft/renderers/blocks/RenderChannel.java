package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityTank;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelValve;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.BaseIcons;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel extends RenderBlockCustomWood<TileEntityChannel> {

	public static AtomicInteger renderCallCounter = new AtomicInteger(0);

    public RenderChannel() {
        this(AgriCraftBlocks.blockWaterChannel, new TileEntityChannel());
    }

	protected RenderChannel(Block block, TileEntityChannel channel) {
		super(block, channel, true, true, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {
		this.renderBottom(tess, teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD);
		this.renderSide(tess, teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.NORTH);
		this.renderSide(tess, teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.EAST);
		this.renderSide(tess, teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.SOUTH);
		this.renderSide(tess, teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.WEST);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityChannel) {
			TileEntityChannel channel = (TileEntityChannel) te;
			this.renderWoodChannel(tess, channel, matIcon, cm);
		}
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntityChannel) {
			TileEntityChannel channel = (TileEntityChannel) te;
			if (channel.getDiscreteFluidLevel() > 0) {
				renderCallCounter.incrementAndGet();
				this.drawWater(tess, channel);
			}
		}
	}

	protected void renderWoodChannel(TessellatorV2 tess, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm) {
		this.renderBottom(tess, channel, matIcon, cm);
		this.renderSide(tess, channel, matIcon, cm, AgriForgeDirection.NORTH);
		this.renderSide(tess, channel, matIcon, cm, AgriForgeDirection.EAST);
		this.renderSide(tess, channel, matIcon, cm, AgriForgeDirection.SOUTH);
		this.renderSide(tess, channel, matIcon, cm, AgriForgeDirection.WEST);
	}

	protected void renderBottom(TessellatorV2 tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm) {
		//bottom
		drawScaledPrism(tessellator, 4, 4, 4, 12, 5, 12, matIcon, cm);
		//corners
		drawScaledPrism(tessellator, 4, 5, 4, 5, 12, 5, matIcon, cm);
		drawScaledPrism(tessellator, 11, 5, 4, 12, 12, 5, matIcon, cm);
		drawScaledPrism(tessellator, 4, 5, 11, 5, 12, 12, matIcon, cm);
		drawScaledPrism(tessellator, 11, 5, 11, 12, 12, 12, matIcon, cm);
	}

	//renders one of the four sides of a channel
	protected void renderSide(TessellatorV2 tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, int cm, AgriForgeDirection dir) {
		if (channel.hasNeighbourCheck(dir)) {
			// extend bottom plane and side edges
			drawScaledPrism(tessellator, 4, 4, 0, 12, 5, 4, matIcon, cm, dir);
			drawScaledPrism(tessellator, 4, 5, 0, 5, 12, 5, matIcon, cm, dir);
			drawScaledPrism(tessellator, 11, 5, 0, 12, 12, 5, matIcon, cm, dir);
		} else {
			// draw an edge
			drawScaledPrism(tessellator, 4, 4, 4, 12, 12, 5, matIcon, cm, dir);
		}
	}

	protected void drawWater(TessellatorV2 tessellator, TileEntityChannel channel) {

		//the texture
		final TextureAtlasSprite icon = BaseIcons.WATER_STILL.getIcon();

		//stolen from Vanilla code
		final int l = Blocks.water.colorMultiplier(channel.getWorld(), channel.getPos());
		final float y = channel.getFluidHeight();
		final float f = (float) (l >> 16 & 255) / 255.0F;
		final float f1 = (float) (l >> 8 & 255) / 255.0F;
		final float f2 = (float) (l & 255) / 255.0F;
		final float f4 = 1.0F;

		//...
		tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(channel.getWorld(), channel.getPos()));
		tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

		//draw central water levels
		drawScaledFaceFrontXZ(tessellator, 5, 5, 11, 11, icon, y - 0.001f, COLOR_MULTIPLIER_STANDARD);
		//connect to edges
		this.connectWater(channel, tessellator, AgriForgeDirection.NORTH, y, icon);
		this.connectWater(channel, tessellator, AgriForgeDirection.EAST, y, icon);
		this.connectWater(channel, tessellator, AgriForgeDirection.SOUTH, y, icon);
		this.connectWater(channel, tessellator, AgriForgeDirection.WEST, y, icon);

	}

	protected void connectWater(TileEntityChannel channel, TessellatorV2 tessellator, AgriForgeDirection direction, float y, TextureAtlasSprite icon) {
		// checks if there is a neighboring block that this block can connect to
		if (channel.hasNeighbourCheck(direction)) {
			IIrrigationComponent te = channel.getNeighbor(direction);
			float y2;
			if (te instanceof TileEntityChannel) {
				if (te instanceof TileEntityChannelValve && ((TileEntityChannelValve) te).isPowered()) {
					y2 = y;
				} else {
					y2 = (y + te.getFluidHeight()) / 2;
				}
			} else {
				float lvl = (te.getFluidHeight() - 16 * ((TileEntityTank) te).getYPosition());
				y2 = lvl > 12 ? 12 : lvl < 5 ? (5 - 0.0001F) : lvl;
			}
			this.drawWaterEdge(tessellator, direction, y, y2, icon);
		}
	}

	protected void drawWaterEdge(TessellatorV2 tessellator, AgriForgeDirection direction, float lvl1, float lvl2, TextureAtlasSprite icon) {
		drawScaledFaceFrontXZ(tessellator, 5, 0, 11, 5, icon, lvl2 - 0.001f, COLOR_MULTIPLIER_STANDARD);
	}
}
