package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.utility.icon.IIconRegistrar;
import com.InfinityRaider.AgriCraft.utility.icon.IconRegisterable;
import com.InfinityRaider.AgriCraft.utility.icon.IconRegistrar;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockBase extends Block implements IconRegisterable {
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite icon;
	
    /**
     * The default, base constructor for all AgriCraft blocks.
     * This method runs the super constructor from the block class, then registers the new block with the {@link RegisterHelper}.
     * 
     * @param mat the {@link Material} the block is comprised of.
     */
    protected BlockBase(Material mat) {
        super(mat);
        RegisterHelper.registerBlock(this, getInternalName(), getItemBlockClass());
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
        return 3;
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

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon() {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegistrar iconRegistrar) {
        this.icon = iconRegistrar.registerIcon("agricraft:textures/blocks/"+this.getUnlocalizedName()+".png");
    }
}
