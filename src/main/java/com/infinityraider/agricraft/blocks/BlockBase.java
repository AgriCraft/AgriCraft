package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.renderers.blocks.ICustomRenderedBlock;
import com.infinityraider.agricraft.tabs.AgriTabs;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * The base class for all AgriCraft blocks.
 */
public abstract class BlockBase extends Block implements ICustomRenderedBlock {

	private final String internalName;

	/**
	 * The default, base constructor for all AgriCraft blocks. This method runs
	 * the super constructor from the block class, then registers the new block
	 * with the {@link RegisterHelper}.
	 *
	 * @param mat the {@link Material} the block is comprised of.
	 * @param internalName the name of the block.
	 */
	protected BlockBase(Material mat, String internalName) {
		super(mat);
		this.internalName = internalName;
		this.fullBlock = false;
		this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
	}
	
	public boolean isEnabled() {
		return true;
	}

	public String getInternalName() {
		return this.internalName;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getBlockModelResourceLocation() {
		return new  ModelResourceLocation("agricraft:"+getInternalName());
	}

	@Override
	protected final BlockStateContainer createBlockState() {
		return new ExtendedBlockState(
				this,
				TypeHelper.asArray(this.getProperties(), IProperty.class), 
				TypeHelper.asArray(this.getUnlistedProperties(), IUnlistedProperty.class)
		);
	}


	/**
	 * @return a property array containing all properties for this block's state
	 */
	public Set<IProperty> getProperties() {
		return new HashSet<>();
	}
	
	/**
	 * @return a property array containing all properties for this block's state
	 */
	public Set<IUnlistedProperty> getUnlistedProperties() {
		return new HashSet<>();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	/**
	 * Retrieves the block's ItemBlock class, as a generic class bounded by the
	 * ItemBlock class.
	 *
	 * @return the block's class, may be null if no specific ItemBlock class is
	 * desired.
	 */
	public abstract Class<? extends ItemBlock> getItemBlockClass();

	/**
	 * @return The default bounding box for this block
	 */
	public abstract AxisAlignedBB getDefaultBoundingBox();

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getDefaultBoundingBox();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public List<ResourceLocation> getTextures() {
		return Collections.emptyList();
	}
	
}
