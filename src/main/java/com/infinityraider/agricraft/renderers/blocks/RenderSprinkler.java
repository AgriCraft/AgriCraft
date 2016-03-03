package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.utility.icon.BaseIcons;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@SideOnly(Side.CLIENT)
public class RenderSprinkler extends RenderBlockAgriCraft {

	// Dimensions
	private static final float MIN_Y = 8.0F;
	private static final float MAX_Y = 12.0F;
	private static final float MIN_C = 7.0F;
	private static final float MAX_C = 9.0F;
	private static final float BLADE_W = 1.0F;
	private static final float BLADE_L = 3.0F;

	// Calculated
	private static final float BMX_Y = MIN_Y + BLADE_W;
	private static final float BMX_A = MIN_C - BLADE_L;
	private static final float BMX_B = MAX_C + BLADE_L;

	public RenderSprinkler() {
		super(AgriCraftBlocks.blockSprinkler, new TileEntitySprinkler(), true, true, true);
	}

	@Override
	protected void doInventoryRender(TessellatorV2 tess, ItemStack item) {
		// Draw Top
		RenderUtil.drawScaledPrism(tess, 4, 8, 4, 12, 16, 12, BaseIcons.OAK_PLANKS.getIcon());
		// Get Core Icon
		final TextureAtlasSprite coreIcon = BaseIcons.IRON_BLOCK.getIcon();
		// Draw Core
		RenderUtil.drawScaledPrism(tess, MIN_C, MIN_Y - 8, MIN_C, MAX_C, MAX_Y - 4, MAX_C, coreIcon);
		// Draw Blades
		RenderUtil.drawScaledPrism(tess, BMX_A, MIN_Y - 8, MIN_C, BMX_B, BMX_Y - 8, MAX_C, coreIcon);
		RenderUtil.drawScaledPrism(tess, MIN_C, MIN_Y - 8, BMX_A, MAX_C, BMX_Y - 8, BMX_B, coreIcon);
	}

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntitySprinkler) {
			final TileEntitySprinkler sprinkler = (TileEntitySprinkler) te;
			tess.translate(0, 4 * Constants.UNIT, 0);
			RenderUtil.drawScaledPrism(tess, 4, 8, 4, 12, 16, 12, sprinkler.getChannelIcon());
		}
	}

	@Override
	protected void doRenderTileEntity(TessellatorV2 tess, TileEntity te) {
		if (te instanceof TileEntitySprinkler) {
			final TileEntitySprinkler sprinkler = (TileEntitySprinkler) te;
			final TextureAtlasSprite icon = BaseIcons.IRON_BLOCK.getIcon();
			tess.rotateBlock(sprinkler.angle, 0, 1, 0);
			// Draw Core
			RenderUtil.drawScaledPrism(tess, MIN_C, MIN_Y, MIN_C, MAX_C, MAX_Y, MAX_C, icon);
			// Draw Blades
			RenderUtil.drawScaledPrism(tess, BMX_A, MIN_Y, MIN_C, BMX_B, BMX_Y, MAX_C, icon);
			RenderUtil.drawScaledPrism(tess, MIN_C, MIN_Y, BMX_A, MAX_C, BMX_Y, BMX_B, icon);
		}
	}

}
