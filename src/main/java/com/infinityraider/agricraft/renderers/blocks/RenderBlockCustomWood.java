package com.infinityraider.agricraft.renderers.blocks;


import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<T extends TileEntityCustomWood> extends RenderBlockBase<T> {
    protected RenderBlockCustomWood(BlockCustomWood<T> block, T te, boolean inventory, boolean tesr, boolean isbrh) {
		super(block, te, inventory, tesr, isbrh);
    }

	@Override
	public final void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
									   @Nullable T tile, boolean dynamicRender, float partialTick, int destroyStage) {
		if(tile != null) {
			this.renderWorldBlock(tessellator, world, pos, x, y, z, state, block, tile, dynamicRender, partialTick, destroyStage, getIcon(tile));
		}
	}

	public abstract void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
										  T tile, boolean dynamicRender, float partialTick, int destroyStage, TextureAtlasSprite icon);

	@Override
	public final void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block,  @Nullable T tile,
										   ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
		if(tile != null) {
			tile.setMaterial(stack);
			this.renderInventoryBlock(tessellator, world, state, block, tile, stack, entity, type, getIcon(tile));
		}
	}

	public abstract void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, T tile,
											  ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type, TextureAtlasSprite icon);

	@Override
	public TextureAtlasSprite getIcon() {
		return getIcon(getTileEntity());
	}

	public TextureAtlasSprite getIcon(TileEntityCustomWood tile) {
		if(tile == null) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		return getIcon(tile.getTexture());
	}
}
