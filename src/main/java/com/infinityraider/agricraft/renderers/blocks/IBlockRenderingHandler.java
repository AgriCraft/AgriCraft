package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public interface IBlockRenderingHandler<T extends TileEntity> extends IItemRenderingHandler {
    /**
     * Gets the block tied to this renderer, used for registering this renderer.
     * A pointer to the Block is saved and referenced.
     *
     * @return the block for this renderer
     */
    Block getBlock();

    /**
     * Gets the TileEntity for this renderer (this should be a new TileEntity which is not physically in a World),
     * it is used for registering this renderer and inventory rendering.
     * The class from this TileEntity and the object passed are saved and referenced,
     * The TileEntity is passed to this renderer for inventory rendering, it is not in a world so you can directly change fields to render it
     * This method may return null if there is no tile entity for this block
     *
     * @return a new TileEntity for this renderer
     */
    T getTileEntity();

    /**
     * Returns a list containing a ResourceLocation for every texture used to render this Block.
     * Passed textures are stitched to the Minecraft texture map and icons can be retrieved from them.
     * @return a list of ResourceLocations
     */
    List<ResourceLocation> getAllTextures();
	
	default void renderDynamic(ITessellator tess, T te, float partialTicks, int destroyStage) {};
	
	default void renderStatic(ITessellator tess, T te, IBlockState state) {};

    /**
     * Called to render the block in an inventory
     * startDrawing() has already been called on the tessellator object.
     *
     * @param tessellator tessellator object to draw quads
     * @param world the world object
     * @param state the state of the block
     * @param block the block
     * @param tile the tile entity passed from getTileEntity() (can be null if there is no tile entity)
     * @param stack stack containing this block as an item
     * @param entity entity holding the stack
     * @param type camera transform type
     */
    void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, Block block,
                              @Nullable T tile, ItemStack stack, EntityLivingBase entity);

	@Override
	default public void renderItem(ITessellator tessellator, World world, ItemStack stack, EntityLivingBase entity) {
		this.renderInventoryBlock(tessellator, world, this.getBlock().getDefaultState(), this.getBlock(), this.getTileEntity(), stack, entity);
	}

    /**
     * Gets the main icon used for this renderer, used for the particle
     * @return the particle icon
     */
    TextureAtlasSprite getIcon();

    /**
     * Checks if this handles inventory rendering.
     * @return true to have 3D inventory rendering
     */
    boolean hasInventoryRendering();

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
