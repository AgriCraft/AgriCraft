package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.IconHelper;
import javax.annotation.Nullable;

import com.infinityraider.infinitylib.render.block.RenderBlockBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<B extends BlockCustomWood<T>, T extends TileEntityCustomWood> extends RenderBlockBase<B, T> {
	
	protected final T dummy;

	protected RenderBlockCustomWood(B block, T te, boolean inventory, boolean staticRender, boolean dynRender) {
		super(block, te, inventory, staticRender, dynRender);
		this.dummy = te;
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, B block,
								 @Nullable T tile, boolean dynamicRender, float partialTick, int destroyStage) {
		this.renderWorldBlockWood(tessellator, world, pos, state, block, tile, getIcon(tile), dynamicRender);
	}

	protected abstract void renderWorldBlockWood(ITessellator tess, World world, BlockPos pos, IBlockState state, B block,
												 T tile, TextureAtlasSprite icon, boolean dynamic);

	@Override
	public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
		if (this.dummy != null) {
			this.dummy.setMaterial(stack);
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
