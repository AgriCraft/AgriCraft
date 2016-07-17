package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.AbstractBlockWaterChannel;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel<T extends TileEntityChannel> extends RenderBlockCustomWood<T> {

	public static AtomicInteger renderCallCounter = new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	protected RenderChannel(AbstractBlockWaterChannel<T> block, TileEntityChannel channel) {
		super(block, (T) channel, true, true, true);
	}

	public RenderChannel(AbstractBlockWaterChannel<T> block) {
		this(block, new TileEntityChannel());
	}

	@Override
	public void renderStaticWood(ITessellator tess, T te, IBlockState state, TextureAtlasSprite sprite) {
		state = te.getState(state);
		this.renderWoodChannel(tess, te, state, sprite);
	}

	@Override
	public void renderDynamicWood(ITessellator tess, T te, float partialTicks, int destroyStage, TextureAtlasSprite sprite) {
		this.drawWater(tess, te, BaseIcons.WATER_STILL.getIcon());
	}

	protected void renderWoodChannel(ITessellator tessellator, T channel, IBlockState state, TextureAtlasSprite icon) {
		this.renderBottom(tessellator, icon);
		this.renderSide(tessellator, channel, AgriForgeDirection.NORTH, state.getValue(AgriProperties.NORTH), icon);
		this.renderSide(tessellator, channel, AgriForgeDirection.EAST, state.getValue(AgriProperties.EAST), icon);
		this.renderSide(tessellator, channel, AgriForgeDirection.SOUTH, state.getValue(AgriProperties.SOUTH), icon);
		this.renderSide(tessellator, channel, AgriForgeDirection.WEST, state.getValue(AgriProperties.WEST), icon);
	}

	protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
		//bottom
		tessellator.drawScaledPrism(4, 4, 4, 12, 5, 12, matIcon);
		//corners
		tessellator.drawScaledPrism(4, 5, 4, 5, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 4, 12, 12, 5, matIcon);
		tessellator.drawScaledPrism(4, 5, 11, 5, 12, 12, matIcon);
		tessellator.drawScaledPrism(11, 5, 11, 12, 12, 12, matIcon);
	}
	
	protected final void renderSide(ITessellator tessellator, T channel, AgriForgeDirection dir, int code, TextureAtlasSprite matIcon) {
		tessellator.pushMatrix();
		RenderUtil.rotateBlock(tessellator, dir);
		renderSideRotated(tessellator, channel, dir, code, matIcon);
		tessellator.popMatrix();
	}

	//renders one of the four sides of a channel
	protected void renderSideRotated(ITessellator tessellator, T channel, AgriForgeDirection dir, int code, TextureAtlasSprite matIcon) {
		if (code == 0) {
			// draw an edge
			tessellator.drawScaledPrism(5, 5, 4, 11, 12, 5, matIcon);
		} else {
			// extend bottom plane and side edges
			tessellator.drawScaledPrism(4, 4, 0, 12, 5, 4, matIcon);
			tessellator.drawScaledPrism(4, 5, 0, 5, 12, 5, matIcon);
			tessellator.drawScaledPrism(11, 5, 0, 12, 12, 5, matIcon);
		}
	}

	protected void drawWater(ITessellator tessellator, T channel, TextureAtlasSprite icon) {
		if (channel.getFluidAmount(0) > 0) {
			renderCallCounter.incrementAndGet();
		} else {
			return;
		}

		//stolen from Vanilla code
		final int l = RenderUtil.getMixedBrightness(channel.getWorld(), channel.getPos(), Blocks.WATER.getDefaultState());
		final float y = channel.getFluidHeight() * Constants.UNIT;
		final float f = (float) (l >> 16 & 255) / 255.0F;
		final float f1 = (float) (l >> 8 & 255) / 255.0F;
		final float f2 = (float) (l & 255) / 255.0F;
		final float f4 = 1.0F;

		//...
		tessellator.setBrightness(l);
		tessellator.setColorRGBA(f4 * f, f4 * f1, f4 * f2, 0.8F);

		//draw central water levels
		tessellator.drawScaledFaceDouble(5, 5, 11, 11, EnumFacing.UP, icon, y - 0.001f);
		//connect to edges
		if (channel.hasNeighbourCheck(AgriForgeDirection.NORTH)) {
			tessellator.drawScaledFaceDouble(5, 0, 11, 5, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.EAST)) {
			tessellator.drawScaledFaceDouble(11, 5, 16, 11, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.SOUTH)) {
			tessellator.drawScaledFaceDouble(5, 11, 11, 16, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(AgriForgeDirection.WEST)) {
			tessellator.drawScaledFaceDouble(0, 5, 5, 11, EnumFacing.UP, icon, y - 0.001f);
		}

	}
}
