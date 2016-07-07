package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.agricraft.utility.IconHelper;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase<TileEntityCrop> {

	private final ResourceLocation[] weedTextures;

	public RenderCrop(BlockCrop block) {
		super(block, new TileEntityCrop(), false, false, true);
		this.weedTextures = new ResourceLocation[16];
		for (int i = 0; i < weedTextures.length; i++) {
			weedTextures[i] = block.getWeedTexture(i);
		}
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
			@Nullable TileEntityCrop crop, boolean dynamicRender, float partialTick, int destroyStage) {
		if (crop != null) {
			if (crop.hasPlant()) {
				//render the plant
				PlantRenderer.renderPlant(world, BlockPos.ORIGIN, crop.getGrowthStage(), crop.getPlant(), tessellator);
			} else if (crop.hasWeed()) {
				//render weeds
				PlantRenderer.renderHashTagPattern(tessellator, tessellator.getIcon(weedTextures[crop.getGrowthStage()]), 0);
			}
		}
	}

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block, @Nullable TileEntityCrop tile,
			ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return IconHelper.getDefaultIcon();
	}
}
