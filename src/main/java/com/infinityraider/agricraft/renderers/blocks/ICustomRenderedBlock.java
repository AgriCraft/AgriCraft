/*
 */
package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.blocks.IBlockRenderingHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author The AgriCraft Team.
 */
@SideOnly(Side.CLIENT)
public interface ICustomRenderedBlock {
	
	/**
     * Gets called to create the IBlockRenderingHandler instance to render this block with
     * @return a new IBlockRenderingHandler object for this block
     */
    IBlockRenderingHandler getRenderer();
	
	/**
     * Gets an array of ResourceLocations used for the model of this block, all block states for this block will use this as key in the model registry
     * @return a unique ModelResourceLocation for this block
     */
    ModelResourceLocation getBlockModelResourceLocation();
	
}
