package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityTank;
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

@SideOnly(Side.CLIENT)
public class RenderTank extends RenderBlockCustomWood<TileEntityTank> {

	public RenderTank() {
		super(AgriCraftBlocks.blockWaterTank, new TileEntityTank(), true, true, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item, TextureAtlasSprite matIcon) {
		drawWoodTank(teDummy, tess);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos, TextureAtlasSprite matIcon, int cm) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) te;
			drawWoodTank(tank, tess);
		}
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank) te;
			drawWater(tank, tess);
		}
	}

	private void drawWoodTank(TileEntityTank tank, TessellatorV2 tessellator) {
		this.renderBottom(tank, tessellator);
		this.renderSide(tank, tessellator, AgriForgeDirection.NORTH);
		this.renderSide(tank, tessellator, AgriForgeDirection.EAST);
		this.renderSide(tank, tessellator, AgriForgeDirection.SOUTH);
		this.renderSide(tank, tessellator, AgriForgeDirection.WEST);
	}

	private void renderBottom(TileEntityTank tank, TessellatorV2 tessellator) {
		//the texture
		TextureAtlasSprite icon = tank.getIcon();
		int cm = tank.colorMultiplier();
		//bottom
		boolean bottom = !tank.hasNeighbour(AgriForgeDirection.DOWN);
		if (bottom) {
			drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon, cm);
		}
		//corners
		int yMin = bottom ? 1 : 0;
		if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
			drawScaledPrism(tessellator, 0, yMin, 0, 2, 16, 2, icon, cm);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.NORTH)) {
			drawScaledPrism(tessellator, 14, yMin, 0, 16, 16, 2, icon, cm);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.WEST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
			drawScaledPrism(tessellator, 0, yMin, 14, 2, 16, 16, icon, cm);
		}
		if (!tank.hasNeighbour(AgriForgeDirection.EAST) || !tank.hasNeighbour(AgriForgeDirection.SOUTH)) {
			drawScaledPrism(tessellator, 14, yMin, 14, 16, 16, 16, icon, cm);
		}
	}

	private void renderSide(TileEntityTank tank, TessellatorV2 tessellator, AgriForgeDirection dir) {
		//the texture
		TextureAtlasSprite icon = tank.getIcon();
		int cm = tank.colorMultiplier();
		int yMin = tank.hasNeighbour(AgriForgeDirection.DOWN) ? 0 : 1;
		if ((dir != null) && (dir != AgriForgeDirection.UNKNOWN)) {
			//connected to a channel
			if (tank.isConnectedToChannel(dir)) {
				drawScaledPrism(tessellator, 2, yMin, 0, 14, 5, 2, icon, cm, dir);
				drawScaledPrism(tessellator, 2, 5, 0, 5, 12, 2, icon, cm, dir);
				drawScaledPrism(tessellator, 11, 5, 0, 14, 12, 2, icon, cm, dir);
				drawScaledPrism(tessellator, 2, 12, 0, 14, 16, 2, icon, cm, dir);
			} //not connected to anything
			else if (!tank.hasNeighbour(dir)) {
				drawScaledPrism(tessellator, 2, yMin, 0, 14, 16, 2, icon, cm, dir);
			}
		}
	}

	private void drawWater(TileEntityTank tank, TessellatorV2 tessellator) {
		//only render water on the bottom layer
		if (tank.getYPosition() == 0) {
			float y = tank.getFluidHeight() - 0.01F; //-0.0001F to avoid Z-fighting on maximum filled tanks
			//the texture
			//stolen from Vanilla code
			/*
			int l = Blocks.water.colorMultiplier(tank.getWorld(), tank.getPos());
			float f = (float) (l >> 16 & 255) / 255.0F;
			float f1 = (float) (l >> 8 & 255) / 255.0F;
			float f2 = (float) (l & 255) / 255.0F;
			float f4 = 1.0F;
			tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(tank.getWorld(), tank.getPos()));
			tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
			 */
			//draw surface
			final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
			addScaledVertexWithUV(tessellator, 0, y, 0, 0, 0, waterIcon);
			addScaledVertexWithUV(tessellator, 0, y, 16, 0, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 16, y, 16, 16, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 16, y, 0, 16, 0, waterIcon);
		}
	}
}
