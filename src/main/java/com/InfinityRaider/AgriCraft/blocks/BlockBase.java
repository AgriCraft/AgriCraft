package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.api.v1.IIconRegistrar;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftRenderable;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockBase extends Block implements IAgriCraftRenderable {

	public static final int DEFAULT_RENDER_TYPE = 2;
	public static final EnumWorldBlockLayer DEFAULT_BLOCK_LAYER = EnumWorldBlockLayer.CUTOUT;
	public static final ItemStack DEFAULT_WAILA_STACK = null;

	public final int renderType;
	public final EnumWorldBlockLayer blockLayer;
	public final String internalName;

	@SideOnly(Side.CLIENT)
	protected TextureAtlasSprite icon;

	/**
	 * The default, base constructor for all AgriCraft blocks. This method runs
	 * the super constructor from the block class, then registers the new block
	 * with the {@link RegisterHelper}.
	 *
	 * @param mat the {@link Material} the block is comprised of.
	 */
	protected BlockBase(Material mat, String internalName) {
		this(mat, internalName, DEFAULT_RENDER_TYPE, DEFAULT_BLOCK_LAYER);
	}

	protected BlockBase(Material mat, String internalName, int renderType, EnumWorldBlockLayer blockLayer) {
		super(mat);
		this.internalName = internalName;
		// The following two do not appear to ever be used...
		this.renderType = renderType;
		this.blockLayer = blockLayer;
		// This might be bad.
		RegisterHelper.registerBlock(this, this.internalName, this.getItemBlockClass());
	}

	// Abstract methods.
	protected abstract IProperty[] getPropertyArray();

	/**
	 * Retrieves the block's renderer.
	 *
	 * @return the block's renderer.
	 */
	@SideOnly(Side.CLIENT)
	public abstract RenderBlockBase getRenderer();

	/**
	 * Retrieves the block's ItemBlock class, as a generic class bounded by the
	 * ItemBlock class.
	 *
	 * @return the block's class, may be null if no specific ItemBlock class is
	 * desired.
	 */
	protected abstract Class<? extends ItemBlock> getItemBlockClass();

	// Pre-Implemented Methods
	/**
	 * TODO: determine function of this method...
	 *
	 * @return
	 */
	@Override
	protected final BlockState createBlockState() {
		return new BlockState(this, getPropertyArray());
	}

	/**
	 * Retrieves the stack to show in waila.
	 *
	 * @param tea tile entity associated with the block, possibly null.
	 */
	public ItemStack getWailaStack(BlockBase block, TileEntityBase tea) {
		return DEFAULT_WAILA_STACK;
	}

	/**
	 * TODO: Determine if icon ever changes, and switch over to constant field.
	 *
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegistrar iconRegistrar) {
		String name = this.getUnlocalizedName();
		int index = name.indexOf(":");
		name = index > 0 ? name.substring(index + 1) : name;
		index = name.indexOf(".");
		name = index > 0 ? name.substring(index + 1) : name;
		this.icon = iconRegistrar.registerIcon("agricraft:blocks/" + name);
	}

}
