package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockBase extends Block {
    private final ResourceLocation texture;
	
    /**
     * The default, base constructor for all AgriCraft blocks.
     * This method runs the super constructor from the block class, then registers the new block with the {@link RegisterHelper}.
     * 
     * @param mat the {@link Material} the block is comprised of.
     */
    protected BlockBase(Material mat) {
        super(mat);
        RegisterHelper.registerBlock(this, getInternalName(), getItemBlockClass());
        this.texture = Block.blockRegistry.getNameForObject(this);
    }

    @Override
    protected final BlockState createBlockState() {
        return new BlockState(this, getPropertyArray());
    }

    protected  abstract IProperty[] getPropertyArray();

    /**
     * Retrieves the block's renderer.
     * 
     * @return the block's renderer.
     */
    @SideOnly(Side.CLIENT)
    public abstract RenderBlockBase getRenderer();

    /**
     * Determines the block's rendering type via {@link AgriCraft#proxy}
     * 
     * @return the block's render type.
     */
    @Override
    public int getRenderType() {
        return AgriCraft.proxy.getRenderId(this);
    }

    /**
     * Retrieves the block's ItemBlock class, as a generic class bounded by the ItemBlock class.
     * 
     * @return the block's class, may be null if no specific ItemBlock class is desired.
     */
    protected abstract Class<? extends ItemBlock> getItemBlockClass();

    /**
     * Retrieves the name of the block used internally.
     * 
     * @return the internal name of the block.
     */
    protected abstract String getInternalName();
    
    /**
     * Retrieves the stack to show in waila.
     * 
     * @param tea tile entity associated with the block, possibly null.
     */
    public ItemStack getWailaStack(BlockBase block, TileEntityBase tea) {
    	return null;
    }

    public  ResourceLocation getTexture() {
        return this.texture;
    }

    /**
     * Gets the texture to render this block with
     * @param world the world object
     * @param pos the block position
     * @param state the block state
     * @param te the tile entity (can be null)
     * @return a ResourceLocation holding the texture
     */
    public ResourceLocation getTexture(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side, @Nullable TileEntityBase te) {
        return this.getTexture();
    }
}
