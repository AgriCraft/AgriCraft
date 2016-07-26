package com.infinityraider.agricraft.renderers.blocks;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.render.block.RenderBlockBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase<BlockCrop, TileEntityCrop> {

	public RenderCrop(BlockCrop block) {
		super(block, new TileEntityCrop(), false, true, false);
	}
	
	static {
		AgriCore.getConfig().addConfigurable(RenderCrop.class);
	}

	@Override
	public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, BlockCrop block,
								 @Nullable TileEntityCrop crop, boolean dynamicRender, float partialTick, int destroyStage) {

		TextureAtlasSprite sprite = this.getIcon(BlockCrop.TEXTURE);
		tessellator.translate(0, -3 * Constants.UNIT, 0);
		tessellator.drawScaledPrism(2, 0, 2, 3, 16, 3, sprite);
		tessellator.drawScaledPrism(13, 0, 2, 14, 16, 3, sprite);
		tessellator.drawScaledPrism(13, 0, 13, 14, 16, 14, sprite);
		tessellator.drawScaledPrism(2, 0, 13, 3, 16, 14, sprite);
		tessellator.translate(0, 3 * Constants.UNIT, 0);
		if (crop != null && crop.isCrossCrop()) {
            tessellator.drawScaledPrism(0, 10, 2, 16, 11, 3, sprite);
            tessellator.drawScaledPrism(0, 10, 13, 16, 11, 14, sprite);
            tessellator.drawScaledPrism(2, 10, 0, 3, 11, 16, sprite);
            tessellator.drawScaledPrism(13, 10, 0, 14, 11, 16, sprite);
		}
		renderPlant(tessellator, world, pos, block, state, crop);

	}

    private void renderPlant(ITessellator tessellator, World world, BlockPos pos, Block block, IBlockState state, TileEntityCrop crop) {
        if(crop.hasPlant()) {
            PlantRenderer.renderPlant(world, pos,  block.getMetaFromState(state), crop.getPlant(), tessellator);
        }
    }

	@Override
	public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockCrop block, @Nullable TileEntityCrop tile,
									 ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

	}

	@Override
	public TextureAtlasSprite getIcon() {
		return getIcon(BlockCrop.TEXTURE);
	}
}
