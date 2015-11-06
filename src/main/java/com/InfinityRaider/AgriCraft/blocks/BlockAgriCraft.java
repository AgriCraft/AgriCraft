package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockAgriCraft extends Block {
	
    /**
     * The default, base constructor for all AgriCraft blocks.
     * This method runs the super constructor from the block class, then registers the new block with the {@link RegisterHelper}.
     * 
     * @param mat the {@link Material} the block is comprised of.
     */
    protected BlockAgriCraft(Material mat) {
        super(mat);
        RegisterHelper.registerBlock(this, getInternalName(), getItemBlockClass());
    }

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
    public ItemStack getWailaStack(BlockAgriCraft block, TileEntityAgricraft tea) {
    	return null;
    }
}
