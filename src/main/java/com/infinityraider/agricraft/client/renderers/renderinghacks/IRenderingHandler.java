package com.infinityraider.agricraft.client.renderers.renderinghacks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderingHandler {
	
    boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPos pos, Block block, IBlockState state, WorldRenderer renderer);
	
	default boolean shouldRender3D(ItemStack stack) {
		return true;
	}

	default void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		// NOP
	}
	
}
