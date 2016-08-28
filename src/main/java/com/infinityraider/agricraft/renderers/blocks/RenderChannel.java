package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.AbstractBlockWaterChannel;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.RenderUtilBase;

import java.util.concurrent.atomic.AtomicInteger;

@SideOnly(Side.CLIENT)
public class RenderChannel<B extends AbstractBlockWaterChannel<T>, T extends TileEntityChannel> extends RenderBlockCustomWood<B, T> {

	public static AtomicInteger renderCallCounter = new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	public RenderChannel(B block, T channel) {
		super(block, channel, true, true, true);
	}

	protected void renderWoodChannel(ITessellator tessellator, T channel, TextureAtlasSprite icon) {
		this.renderBottom(tessellator, icon);
		this.renderSide(tessellator, channel, EnumFacing.NORTH, channel.hasNeighbourCheck(EnumFacing.NORTH), icon);
		this.renderSide(tessellator, channel, EnumFacing.EAST, channel.hasNeighbourCheck(EnumFacing.EAST), icon);
		this.renderSide(tessellator, channel, EnumFacing.SOUTH, channel.hasNeighbourCheck(EnumFacing.SOUTH), icon);
		this.renderSide(tessellator, channel, EnumFacing.WEST, channel.hasNeighbourCheck(EnumFacing.WEST), icon);
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
	
	protected void renderSide(ITessellator tessellator, T channel, EnumFacing dir, boolean connect, TextureAtlasSprite matIcon) {
		switch(dir) {
            case EAST:
                //positive x
                if(connect) {
                    tessellator.drawScaledPrism(12, 4, 4, 16, 5, 12, matIcon);
                    tessellator.drawScaledPrism(12, 5, 4, 16, 12, 5, matIcon);
                    tessellator.drawScaledPrism(12, 5, 11, 16, 12, 12, matIcon);
                } else {
                    tessellator.drawScaledPrism(11, 5, 5, 12, 12, 11, matIcon);
                }
                break;
            case WEST:
                //negative x
                if(connect) {
                    tessellator.drawScaledPrism(0, 4, 4, 4, 5, 12, matIcon);
                    tessellator.drawScaledPrism(0, 5, 4, 4, 12, 5, matIcon);
                    tessellator.drawScaledPrism(0, 5, 11, 4, 12, 12, matIcon);
                } else {
                    tessellator.drawScaledPrism(4, 5, 5, 5, 12, 11, matIcon);
                }
                break;
            case NORTH:
                //negative z
                if(connect) {
                    tessellator.drawScaledPrism(4, 4, 0, 12, 5, 4, matIcon);
                    tessellator.drawScaledPrism(4, 5, 0, 5, 12, 4, matIcon);
                    tessellator.drawScaledPrism(11, 5, 0, 12, 12, 4, matIcon);
                } else {
                    tessellator.drawScaledPrism(5, 5, 4, 11, 12, 5, matIcon);
                }
                break;
            case SOUTH:
                //positive z
                if(connect) {
                    tessellator.drawScaledPrism(4, 4, 12, 12, 5, 16, matIcon);
                    tessellator.drawScaledPrism(4, 5, 12, 5, 12, 16, matIcon);
                    tessellator.drawScaledPrism(11, 5, 12, 12, 12, 16, matIcon);
                } else {
                    tessellator.drawScaledPrism(5, 5, 11, 11, 12, 12, matIcon);
                }
                break;
        }
	}

	protected void drawWater(ITessellator tessellator, T channel, TextureAtlasSprite icon) {
		if (channel.getFluidAmount(0) > 0) {
			renderCallCounter.incrementAndGet();
		} else {
			return;
		}

		//stolen from Vanilla code
		final int l = RenderUtilBase.getMixedBrightness(channel.getWorld(), channel.getPos(), Blocks.WATER.getDefaultState());
		final float y = channel.getFluidHeight() * Constants.UNIT;
		final float f = (float) (l >> 16 & 255) / 255.0F;
		final float f1 = (float) (l >> 8 & 255) / 255.0F;
		final float f2 = (float) (l & 255) / 255.0F;
		final float f4 = 1.0F;

		//...
		tessellator.setBrightness(l);
		tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);

		//draw central water levels
		tessellator.drawScaledFaceDouble(5, 5, 11, 11, EnumFacing.UP, icon, y - 0.001f);
		//connect to edges
		if (channel.hasNeighbourCheck(EnumFacing.NORTH)) {
			tessellator.drawScaledFaceDouble(5, 0, 11, 5, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(EnumFacing.EAST)) {
			tessellator.drawScaledFaceDouble(11, 5, 16, 11, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(EnumFacing.SOUTH)) {
			tessellator.drawScaledFaceDouble(5, 11, 11, 16, EnumFacing.UP, icon, y - 0.001f);
		}
		if (channel.hasNeighbourCheck(EnumFacing.WEST)) {
			tessellator.drawScaledFaceDouble(0, 5, 5, 11, EnumFacing.UP, icon, y - 0.001f);
		}

	}

	@Override
	public void renderDynamicTile(ITessellator tess, T tile, float partialTicks, int destroyStage) {
		this.drawWater(tess, tile, BaseIcons.WATER_STILL.getIcon());
	}

	@Override
	protected final void renderStaticWood(ITessellator tess, T tile, TextureAtlasSprite matIcon) {
		this.renderWoodChannel(tess, tile, matIcon);
	}

	@Override
	public void renderInventoryBlockWood(ITessellator tessellator, World world, T channel, ItemStack stack, EntityLivingBase entity, TextureAtlasSprite icon) {
		this.renderBottom(tessellator, icon);
		this.renderSide(tessellator, channel, EnumFacing.NORTH, false, icon);
		this.renderSide(tessellator, channel, EnumFacing.EAST, false, icon);
		this.renderSide(tessellator, channel, EnumFacing.SOUTH, false, icon);
		this.renderSide(tessellator, channel, EnumFacing.WEST, false, icon);

	}

	@Override
	public boolean applyAmbientOcclusion() {
		return false;
	}
}
