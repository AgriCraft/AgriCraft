package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.IconHelper;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<T extends TileEntityCustomWood> extends RenderBlockBase<T> {

	protected RenderBlockCustomWood(BlockCustomWood<T> block, T te, boolean inventory, boolean tesr, boolean isbrh) {
		super(block, te, inventory, tesr, isbrh);
	}

	@Override
	public final void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, @Nullable T tile,
			ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
		if (tile != null) {
			tile.setMaterial(stack);
			this.renderInventoryBlockWood(tessellator, world, state, block, tile, stack, entity, type, getIcon(tile));
		}
	}

	public void renderInventoryBlockWood(ITessellator tess, World world, IBlockState state, Block block, T tile,
			ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon) {
		renderStatic(tess, tile, tile.getState(block.getDefaultState()));
	}

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

	@Override
	public final void renderStatic(ITessellator tess, T te, IBlockState state) {
		//WoodType type = state.getValue(AgriProperties.WOOD_TYPE);
		//TextureAtlasSprite sprite = tess.getIcon(new ResourceLocation(type.getTexture()));
		this.renderStaticWood(tess, te, state, this.getIcon());
	}
	
	protected void renderStaticWood(ITessellator tess, T te, IBlockState state, TextureAtlasSprite sprite) {
	}

	@Override
	public final void renderDynamic(ITessellator tess, T te, float partialTicks, int destroyStage) {
		this.renderDynamicWood(tess, te, partialTicks, destroyStage, this.getIcon());
	}
	
	protected void renderDynamicWood(ITessellator tess, T te, float partialTicks, int destroyStage, TextureAtlasSprite sprite) {
	}
	
}
