package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.tileentity.TileEntityBase;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;
import com.infinityraider.agricraft.utility.icon.IconUtil;
import com.infinityraider.agricraft.renderers.renderinghacks.IRenderingHandler;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockBase extends Block implements IAgriCraftRenderable {

	public static final int DEFAULT_RENDER_TYPE = 2;
	public static final BlockRenderLayer DEFAULT_BLOCK_LAYER = BlockRenderLayer.CUTOUT;
	public static final ItemStack DEFAULT_WAILA_STACK = null;

	public final int renderType;
	public final BlockRenderLayer blockLayer;
	public final String internalName;
	private final AxisAlignedBB box;

	@SideOnly(Side.CLIENT)
	protected TextureAtlasSprite icon;

	/**
	 * The default, base constructor for all AgriCraft blocks. This method runs
	 * the super constructor from the block class, then registers the new block
	 * with the {@link RegisterHelper}.
	 *
	 * @param mat the {@link Material} the block is comprised of.
	 * @param internalName the name of the block.
	 */
	protected BlockBase(Material mat, String internalName, AxisAlignedBB box) {
		this(mat, internalName, DEFAULT_RENDER_TYPE, box, DEFAULT_BLOCK_LAYER);
	}

	protected BlockBase(Material mat, String internalName) {
		this(mat, internalName, DEFAULT_RENDER_TYPE, Block.FULL_BLOCK_AABB, DEFAULT_BLOCK_LAYER);
	}

	protected BlockBase(Material mat, String internalName, int renderType, BlockRenderLayer blockLayer) {
		this(mat, internalName, DEFAULT_RENDER_TYPE, Block.FULL_BLOCK_AABB, DEFAULT_BLOCK_LAYER);
	}

	protected BlockBase(Material mat, String internalName, int renderType, AxisAlignedBB box, BlockRenderLayer blockLayer) {
		super(mat);
		this.internalName = internalName;
		// The following two do not appear to ever be used...
		this.renderType = renderType;
		this.blockLayer = blockLayer;
		this.fullBlock = false;
		this.box = box;
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
	public abstract IRenderingHandler getRenderer();

	/**
	 * Retrieves the block's ItemBlock class, as a generic class bounded by the
	 * ItemBlock class.
	 *
	 * @return the block's class, may be null if no specific ItemBlock class is
	 * desired.
	 */
	protected abstract Class<? extends ItemBlock> getItemBlockClass();

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return box;
	}

	// Pre-Implemented Methods
	/**
	 * TODO: determine function of this method...
	 *
	 * @return
	 */
	@Override
	protected final BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyArray());
	}

	/**
	 * Retrieves the stack to show in waila.
	 *
	 * @param tea tile entity associated with the block, possibly null.
	 */
	public ItemStack getWailaStack(BlockBase block, TileEntityBase tea) {
		return DEFAULT_WAILA_STACK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons() {
		String name = this.getUnlocalizedName();
		int index = name.indexOf(":");
		name = index > 0 ? name.substring(index + 1) : name;
		index = name.indexOf(".");
		name = index > 0 ? name.substring(index + 1) : name;
		this.icon = IconUtil.registerIcon("agricraft:blocks/" + name);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return DEFAULT_BLOCK_LAYER;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}

}
