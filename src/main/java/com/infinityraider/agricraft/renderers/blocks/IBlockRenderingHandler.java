package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public interface IBlockRenderingHandler<T extends TileEntity> {
    /**
     * Gets the block tied to this renderer, used for registering this renderer.
     * A pointer to the Block is saved and referenced.
     *
     * @return the block for this renderer
     */
    Block getBlock();

    /**
     * Gets the TileEntity for this renderer, used for registering this renderer.
     * The class from this TileEntity is saved and referenced, the object passed is discarded after registering.
     * This method may return null if there is no tile entity and/or the renderer doesn't have dynamic behaviour
     *
     * @return a new TileEntity for this renderer
     */
    @Nullable T getTileEntity();

    /**
     * Called to render the block at a specific place in the world,
     * startDrawing() has already been called on the tessellator object.
     * The tessellator is also translated to the block's position
     *
     * @param tessellator tessellator object to draw quads
     * @param world the world for the block
     * @param pos the position for the block
     * @param x the precise x-position of the block (only relevant for TESR calls)
     * @param y the precise y-position of the block (only relevant for TESR calls)
     * @param z the precise z-position of the block (only relevant for TESR calls)
     * @param state the state of the block
     * @param block the block
     * @param tile the tile entity (can be null if there is no tile entity)
     * @param dynamicRender true if called from a TESR, false if not, can be useful if the block has both dynamic and static rendering
     * @param partialTick partial tick, only useful for dynamic rendering
     * @param destroyStage destroy stage, only useful for dynamic rendering
     */
    void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z,
                          IBlockState state, Block block, @Nullable T tile, boolean dynamicRender, float partialTick, int destroyStage);

    /**
     * Gets the main icon used for this renderer, used for the particle
     * @return the particle icon
     */
    TextureAtlasSprite getIcon();

    /**
     * Checks if this should have 3D rendering in inventories
     * @return true to have 3D inventory rendering
     */
    boolean doInventoryRendering();

    /**
     * Return true from here to have this renderer have dynamic behaviour,
     * meaning the vertex buffer is reloaded every render tick (TESR behaviour).
     * If the renderer has dynamic behaviour, getTileEntity() should not return null.
     *
     * @return if this renderer has dynamic rendering behaviour
     */
    boolean hasDynamicRendering();

    /**
     * Return true from here to have this renderer have static behaviour,
     * meaning the vertex buffer is only reloaded on a chunk update.
     *
     * @return true if this renderer has static dynamic rendering behaviour
     */
    boolean hasStaticRendering();
}
