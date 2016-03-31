package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.icon.BaseIcons;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel<T extends TileEntityChannel> extends RenderBlockCustomWood<T> {
	public static AtomicInteger renderCallCounter = new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	protected RenderChannel(Block block, TileEntityChannel channel) {
		super(block, (T) channel, true, true, true);
	}
	
	public RenderChannel() {
		this(AgriCraftBlocks.blockWaterChannel, new TileEntityChannel());
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
								 TileEntityChannel channel, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite matIcon) {
		if (dynamicRender) {
			this.drawWater(tessellator, channel);
		} else {
			this.renderWoodChannel(tessellator, channel, matIcon);
		}
	}

	/*
	@Override
	protected void doInventoryRender(TessellatorV2 ItemStack item, TextureAtlasSprite matIcon) {
		this.renderBottom(teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD);
		this.renderSide(teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.NORTH);
		this.renderSide(teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.EAST);
		this.renderSide(teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.SOUTH);
		this.renderSide(teDummy, matIcon, RenderUtil.COLOR_MULTIPLIER_STANDARD, AgriForgeDirection.WEST);
	}
	*/

	protected void renderWoodChannel(ITessellator tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon) {
		this.renderBottom(tessellator, matIcon);
		this.renderSide(tessellator,channel, matIcon, AgriForgeDirection.NORTH);
		this.renderSide(tessellator,channel, matIcon, AgriForgeDirection.EAST);
		this.renderSide(tessellator,channel, matIcon, AgriForgeDirection.SOUTH);
		this.renderSide(tessellator,channel, matIcon, AgriForgeDirection.WEST);
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

	//renders one of the four sides of a channel
	protected void renderSide(ITessellator tessellator, TileEntityChannel channel, TextureAtlasSprite matIcon, AgriForgeDirection dir) {
		if (channel.hasNeighbourCheck(dir)) {
			// extend bottom plane and side edges
			tessellator.drawScaledPrism(4, 4, 0, 12, 5, 4, matIcon);
			tessellator.drawScaledPrism(4, 5, 0, 5, 12, 5, matIcon);
			tessellator.drawScaledPrism(11, 5, 0, 12, 12, 5, matIcon);
		} else {
			// draw an edge
			tessellator.drawScaledPrism(4, 4, 4, 12, 12, 5, matIcon);
		}
	}

	protected void drawWater(ITessellator tessellator, TileEntityChannel channel) {
		if (channel.getFluidLevel() > 0) {
			renderCallCounter.incrementAndGet();
		} else {
			return;
		}

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
