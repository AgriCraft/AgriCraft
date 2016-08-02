package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.storage.BlockSeedStorage;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.blocks.tiles.storage.TileEntitySeedStorage;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.RenderUtilBase;

@SideOnly(Side.CLIENT)
public class RenderSeedStorage extends RenderBlockCustomWood<BlockSeedStorage, TileEntitySeedStorage> {

	public RenderSeedStorage(BlockSeedStorage block) {
		super(block, new TileEntitySeedStorage(), true, true, true);
	}

	private void renderSides(ITessellator tessellator, TextureAtlasSprite matIcon) {
		//casing
		tessellator.drawScaledPrism(0, 0, 0, 16, 1, 16, matIcon);
		tessellator.drawScaledPrism(0, 15, 0, 16, 16, 16, matIcon);
		tessellator.drawScaledPrism(0, 1, 0, 1, 15, 16, matIcon);
		tessellator.drawScaledPrism(15, 1, 0, 16, 15, 16, matIcon);
		tessellator.drawScaledPrism(1, 1, 15, 15, 15, 16, matIcon);

		//drawer
		tessellator.drawScaledPrism(1.1F, 1.1F, 1, 14.9F, 14.9F, 2, matIcon);
		tessellator.drawScaledPrism(4, 3, 0, 5, 10, 1, matIcon);
		tessellator.drawScaledPrism(11, 3, 0, 12, 10, 1, matIcon);
		tessellator.drawScaledPrism(4, 10, 0, 12, 11, 1, matIcon);
		tessellator.drawScaledPrism(4, 3, 0, 12, 4, 1, matIcon);

		//handle
		tessellator.drawScaledPrism(7, 12, 0, 9, 13, 1, BaseIcons.IRON_BLOCK.getIcon());

		//trace
		tessellator.drawScaledFace(1, 1, 1.5F, 15, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(14.5F, 1, 15, 15, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(1, 14.5F, 15F, 15, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(1, 1, 15, 1.5F, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(3.5F, 2.5F, 5.5F, 11.5F, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(10.5F, 2.5F, 12.5F, 11.5F, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(3.5F, 2.5F, 12.5F, 4.5F, EnumFacing.NORTH, matIcon, 0.99F);
		tessellator.drawScaledFace(3.5F, 9.5F, 12.5F, 11.5F, EnumFacing.NORTH, matIcon, 0.99F);
	}

	/**
	 * Render the seed as TESR
	 */
	private void drawSeed(ItemStack seed) {
		if (seed == null || seed.getItem() == null) {
			return;
		}

		float dx = 8 * Constants.UNIT;
		float dy = 5 * Constants.UNIT;
		float dz = 0.99F * Constants.UNIT;
		float f = 0.75F;

		RenderUtilBase.renderItemStack(seed, dx, dy, dz, f, false);
	}

	@Override
	protected void renderStaticWood(ITessellator tess, TileEntitySeedStorage tile, TextureAtlasSprite icon) {
		tess.pushMatrix();
		RenderUtilBase.rotateBlock(tess, tile.getOrientation());
		renderSides(tess, icon);
		tess.popMatrix();
	}

	@Override
	public void renderDynamicTile(ITessellator tess, TileEntitySeedStorage tile, float partialTicks, int destroyStage) {
		tess.pushMatrix();
		RenderUtilBase.rotateBlock(tess, tile.getOrientation());
		drawSeed(tile.getLockedSeed());
		tess.popMatrix();
	}

	@Override
	public void renderInventoryBlockWood(ITessellator tess, World world, TileEntitySeedStorage tile, ItemStack stack, EntityLivingBase entity, TextureAtlasSprite icon) {
		renderSides(tess, icon);
	}

	@Override
	public boolean applyAmbientOcclusion() {
		return true;
	}
}
