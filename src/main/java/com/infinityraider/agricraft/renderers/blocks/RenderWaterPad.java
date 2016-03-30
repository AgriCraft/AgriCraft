package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockWaterPad;
import com.infinityraider.agricraft.blocks.BlockWaterPadFull;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.renderers.RenderUtil.*;
import com.infinityraider.agricraft.utility.icon.BaseIcons;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockAgriCraft {

	public RenderWaterPad(Block block) {
		super(block, null, true, false, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item) {

		// Icon
		final TextureAtlasSprite dirtIcon = BaseIcons.DIRT.getIcon();

		// Draw
		drawScaledPrism(tess, 0, 0, 0, 16, 8, 16, dirtIcon);
		drawScaledPrism(tess, 1, 8, 0, 1, 15, 16, dirtIcon);
		drawScaledPrism(tess, 15, 8, 1, 16, 15, 16, dirtIcon);
		drawScaledPrism(tess, 0, 8, 15, 15, 15, 16, dirtIcon);
		drawScaledPrism(tess, 0, 8, 0, 15, 1, 15, dirtIcon);

		// Full
		if (((ItemBlock) item.getItem()).block instanceof BlockWaterPadFull) {
			TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();
			drawScaledPrism(tess, 1, 14, 1, 15, 15, 15, waterIcon);
		}

	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {

		// Check Full
		final boolean full = block instanceof BlockWaterPadFull;

		// Render
		this.renderBase(tess, world, pos, full);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.NORTH);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.EAST);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.SOUTH);
		this.renderSide(tess, world, pos, full, AgriForgeDirection.WEST);

	}

	private void renderBase(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos, boolean full) {

		// Get Icon
		final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();

		//tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
		tessellator.setColorRGBA_F(1, 1, 1, 1);

		//renderer.setRenderBounds(0, 0, 0, 16 * u, 8 * u, 16 * u);
		//renderer.renderStandardBlock(Blocks.dirt, pos);
		//boolean renderAllFaces = renderer.renderAllFaces;
		//renderer.renderAllFaces = true;
		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.WEST, AgriForgeDirection.NORTH)) {
			//renderer.setRenderBounds(0, 8 * u, 0, u, 15 * u, 1 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			setupColor(tessellator, world, pos);
			tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
			addScaledVertexWithUV(tessellator, 0, 14, 0, 0, 0, waterIcon);
			addScaledVertexWithUV(tessellator, 0, 14, 1, 0, 1, waterIcon);
			addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, waterIcon);
			addScaledVertexWithUV(tessellator, 1, 14, 0, 1, 0, waterIcon);
			tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.NORTH, AgriForgeDirection.EAST)) {
			//renderer.setRenderBounds(15 * u, 8 * u, 0, 16 * u, 15 * u, 1 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			setupColor(tessellator, world, pos);
			tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
			addScaledVertexWithUV(tessellator, 15, 14, 0, 15, 0, waterIcon);
			addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, waterIcon);
			addScaledVertexWithUV(tessellator, 16, 14, 1, 16, 1, waterIcon);
			addScaledVertexWithUV(tessellator, 16, 14, 0, 16, 0, waterIcon);
			tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.EAST, AgriForgeDirection.SOUTH)) {
			//renderer.setRenderBounds(15 * u, 8 * u, 15 * u, 16 * u, 15 * u, 16 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			setupColor(tessellator, world, pos);
			tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
			addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, waterIcon);
			addScaledVertexWithUV(tessellator, 15, 14, 16, 15, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 16, 14, 16, 16, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 16, 14, 15, 16, 15, waterIcon);
			tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		if (shouldRenderCorner(world, pos, full, AgriForgeDirection.SOUTH, AgriForgeDirection.WEST)) {
			//renderer.setRenderBounds(0, 8 * u, 15 * u, u, 15 * u, 16 * u);
			//renderer.renderStandardBlock(Blocks.farmland, pos);
		} else if (full) {
			setupColor(tessellator, world, pos);
			tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
			addScaledVertexWithUV(tessellator, 0, 14, 15, 0, 15, waterIcon);
			addScaledVertexWithUV(tessellator, 0, 14, 16, 0, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 1, 14, 16, 1, 16, waterIcon);
			addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, waterIcon);
			tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}

		//renderer.renderAllFaces = renderAllFaces;
		if (full) {
			setupColor(tessellator, world, pos);
			tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
			addScaledVertexWithUV(tessellator, 1, 14, 1, 1, 1, waterIcon);
			addScaledVertexWithUV(tessellator, 1, 14, 15, 1, 15, waterIcon);
			addScaledVertexWithUV(tessellator, 15, 14, 15, 15, 15, waterIcon);
			addScaledVertexWithUV(tessellator, 15, 14, 1, 15, 1, waterIcon);
			tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		}
	}

	private static void setupColor(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos) {
//		int l = Blocks.water.colorMultiplier(world, pos);
//		float f = (float) (l >> 16 & 255) / 255.0F;
//		float f1 = (float) (l >> 8 & 255) / 255.0F;
//		float f2 = (float) (l & 255) / 255.0F;
//		float f4 = 1.0F;
//		tessellator.setBrightness(Blocks.water.getMixedBrightnessForBlock(world, pos));
//		tessellator.setColorRGBA_F(f4 * f, f4 * f1, f4 * f2, 0.8F);
	}

	private boolean shouldRenderCorner(IBlockAccess world, BlockPos pos, boolean full, AgriForgeDirection dir1, AgriForgeDirection dir2) {
		Block block = world.getBlockState(pos.add(dir1.offsetX, 0, dir1.offsetZ)).getBlock();
		boolean flag1 = block instanceof BlockWaterPad;
		boolean flag2 = block instanceof BlockWaterPadFull;
		if (!flag1 || (full != flag2)) {
			return true;
		}
		block = world.getBlockState(pos.add(dir2.offsetX, 0, dir2.offsetZ)).getBlock();
		flag1 = block instanceof BlockWaterPad;
		flag2 = block instanceof BlockWaterPadFull;
		if (!flag1 || (full != flag2)) {
			return true;
		}
		block = world.getBlockState(pos.add(dir1.offsetX + dir2.offsetX, 0, dir1.offsetZ + dir2.offsetZ)).getBlock();
		flag1 = block instanceof BlockWaterPad;
		flag2 = block instanceof BlockWaterPadFull;
		return !flag1 || (full != flag2);
	}

	private void renderSide(TessellatorV2 tessellator, IBlockAccess world, BlockPos pos, boolean full, AgriForgeDirection side) {
		float u = Constants.UNIT;
		int xLower = Math.max(0, 1 + 14 * side.offsetX);
		int xUpper = Math.min(16, 15 + 14 * side.offsetX);
		int zLower = Math.max(0, 1 + 14 * side.offsetZ);
		int zUpper = Math.min(16, 15 + 14 * side.offsetZ);
		Block block = world.getBlockState(pos.add(side.offsetX, 0, side.offsetZ)).getBlock();
		if (block != null && block instanceof BlockWaterPad) {
			boolean flag = block instanceof BlockWaterPadFull;
			if (full) {
				TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO: get water icon
				setupColor(tessellator, world, pos);
				tessellator.translate(pos.getX(), pos.getY(), pos.getZ());
				addScaledVertexWithUV(tessellator, xLower, 14, zLower, xLower, zLower, icon);
				addScaledVertexWithUV(tessellator, xLower, 14, zUpper, xLower, zUpper, icon);
				addScaledVertexWithUV(tessellator, xUpper, 14, zUpper, xUpper, zUpper, icon);
				addScaledVertexWithUV(tessellator, xUpper, 14, zLower, xUpper, zLower, icon);
				tessellator.translate(-pos.getX(), -pos.getY(), -pos.getZ());
			}
			if (flag == full) {
				return;
			}
		}
		//tessellator.setBrightness(Blocks.farmland.getMixedBrightnessForBlock(world, pos));
		tessellator.setColorRGBA_F(1, 1, 1, 1);
		//boolean renderAllFaces = renderer.renderAllFaces;
		//renderer.renderAllFaces = true;
		//renderer.setRenderBounds(xLower * u, 8 * u, zLower * u, xUpper * u, 15 * u, zUpper * u);
		//renderer.renderStandardBlock(Blocks.farmland, pos);
		//renderer.renderAllFaces = renderAllFaces;
	}

}
