package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
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
			if (channel.getFluidLevel() > 0) {
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
		final int l = RenderUtil.getMixedBrightness(channel.getWorld(), channel.getPos(), Blocks.water.getDefaultState());
		final float y = channel.getFluidHeight() * Constants.UNIT;
		final float f = (float) (l >> 16 & 255) / 255.0F;
		final float f1 = (float) (l >> 8 & 255) / 255.0F;
		final float f2 = (float) (l & 255) / 255.0F;
		final float f4 = 1.0F;

		//...
		tessellator.setBrightness(l);
		tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

		//draw central water levels
		drawScaledFaceDoubleXZ(tessellator, 5, 5, 11, 11, icon, y - 0.001f);
		//connect to edges
		if (channel.hasNeighbourCheck(AgriForgeDirection.NORTH)) {
			drawScaledFaceDoubleXZ(tessellator, 5, 0, 11, 5, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.EAST)) {
			drawScaledFaceDoubleXZ(tessellator, 11, 5, 16, 11, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.SOUTH)) {
			drawScaledFaceDoubleXZ(tessellator, 5, 11, 11, 16, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.WEST)) {
			drawScaledFaceDoubleXZ(tessellator, 0, 5, 5, 11, icon, y - 0.001f);
		}

	}

}
