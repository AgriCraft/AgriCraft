package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockSprinkler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.utility.BaseIcons;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderSprinkler extends RenderBlockBase<TileEntitySprinkler> {
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

	public RenderSprinkler(BlockSprinkler block) {
		super(block, new TileEntitySprinkler(), true, true, true);
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, IBlockState state, Block block,
								 @Nullable TileEntitySprinkler sprinkler, boolean dynamicRender, float partialTick, int destroyStage) {
		if(sprinkler != null) {
			if(dynamicRender) {
				tessellator.translate(0.5F, 0, 0.5F);
				tessellator.rotate(sprinkler.angle, 0, 1, 0);
				tessellator.translate(-0.5F, 0, -0.5F);

				final TextureAtlasSprite icon = BaseIcons.IRON_BLOCK.getIcon();
				// Draw Core
				tessellator.drawScaledPrism(MIN_C, MIN_Y, MIN_C, MAX_C, MAX_Y, MAX_C, icon);
				// Draw Blades
				tessellator.drawScaledPrism(BMX_A, MIN_Y, MIN_C, BMX_B, BMX_Y, MAX_C, icon);
				tessellator.drawScaledPrism(MIN_C, MIN_Y, BMX_A, MAX_C, BMX_Y, BMX_B, icon);
			//} else {
				tessellator.translate(0, 4 * Constants.UNIT, 0);
				tessellator.drawScaledPrism(4, 8, 4, 12, 16, 12, sprinkler.getChannelIcon());
			}
		}
	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, @Nullable TileEntitySprinkler tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
		// Draw Top
		tessellator.drawScaledPrism(4, 8, 4, 12, 16, 12, BaseIcons.OAK_PLANKS.getIcon());
		// Get Core Icon
		final TextureAtlasSprite coreIcon = BaseIcons.IRON_BLOCK.getIcon();
		// Draw Core
		tessellator.drawScaledPrism(MIN_C, MIN_Y - 8, MIN_C, MAX_C, MAX_Y - 4, MAX_C, coreIcon);
		// Draw Blades
		tessellator.drawScaledPrism(BMX_A, MIN_Y - 8, MIN_C, BMX_B, BMX_Y - 8, MAX_C, coreIcon);
		tessellator.drawScaledPrism(MIN_C, MIN_Y - 8, BMX_A, MAX_C, BMX_Y - 8, BMX_B, coreIcon);

	}

	@Override
	public TextureAtlasSprite getIcon() {
		return getTileEntity().getChannelIcon();
	}
}
