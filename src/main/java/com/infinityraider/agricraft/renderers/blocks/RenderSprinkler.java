package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockSprinkler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.block.RenderBlockTile;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;


@SideOnly(Side.CLIENT)
public class RenderSprinkler extends RenderBlockTile<BlockSprinkler, TileEntitySprinkler> {

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
	protected void renderStaticTile(ITessellator tess, TileEntitySprinkler tile) {
		tess.pushMatrix();
		tess.translate(0, 4 * Constants.UNIT, 0);
		// TODO: TEMP!
		tess.drawScaledPrism(4, 8, 4, 12, 16, 12, BaseIcons.OAK_PLANKS.getIcon());
		tess.popMatrix();
	}

	@Override
	public void renderDynamicTile(ITessellator tess, TileEntitySprinkler te, float partialTicks, int destroyStage) {
		tess.translate(0.5F, 0, 0.5F);
		tess.rotate(te.angle, 0, 1, 0);
		tess.translate(-0.5F, 0, -0.5F);

		final TextureAtlasSprite icon = BaseIcons.IRON_BLOCK.getIcon();
		// Draw Core
		tess.drawScaledPrism(MIN_C, MIN_Y, MIN_C, MAX_C, MAX_Y, MAX_C, icon);
		// Draw Blades
		tess.drawScaledPrism(BMX_A, MIN_Y, MIN_C, BMX_B, BMX_Y, MAX_C, icon);
		tess.drawScaledPrism(MIN_C, MIN_Y, BMX_A, MAX_C, BMX_Y, BMX_B, icon);
	}

	@Override
	public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
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

	@Override
	public boolean applyAmbientOcclusion() {
		return false;
	}
}
