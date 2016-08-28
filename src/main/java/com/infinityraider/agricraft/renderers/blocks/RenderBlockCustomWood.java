package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.IconHelper;
import com.infinityraider.infinitylib.render.block.RenderBlockTile;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<B extends BlockCustomWood<T>, T extends TileEntityCustomWood> extends RenderBlockTile<B, T> {

	protected RenderBlockCustomWood(B block, T te, boolean inventory, boolean staticRender, boolean dynRender) {
		super(block, te, inventory, staticRender, dynRender);
	}

	@Override
	protected void renderStaticTile(ITessellator tess, T tile) {
		this.renderStaticWood(tess, tile, getIcon(tile));
	}
	
	protected abstract void renderStaticWood(ITessellator tess, T tile, TextureAtlasSprite matIcon);

	@Override
	public final void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
		final T dummy = this.getTileEntity();
		if (dummy != null) {
			dummy.setMaterial(stack);
			this.renderInventoryBlockWood(tessellator, world, dummy, stack, entity, getIcon(dummy));
		}
	}

	protected abstract void renderInventoryBlockWood(ITessellator tess, World world, T dummy, ItemStack stack, EntityLivingBase entity, TextureAtlasSprite icon);

	@Override
	public TextureAtlasSprite getIcon() {
		return getIcon(getTileEntity());
	}

	public TextureAtlasSprite getIcon(TileEntityCustomWood tile) {
		if (tile == null) {
			return BaseIcons.OAK_PLANKS.getIcon();
		}
		return IconHelper.getIcon(tile.getTexture().toString());
	}
	
}
