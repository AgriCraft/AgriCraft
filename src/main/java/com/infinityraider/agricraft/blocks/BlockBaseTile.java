package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.tiles.TileEntityBase;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.multiblock.IMultiBlockComponent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The base class for all AgriCraft tile blocks.
 */
public abstract class BlockBaseTile<T extends TileEntityBase> extends BlockBase<T> implements ITileEntityProvider {
	public final boolean isMultiBlock;
	public final String tileName;

	/**
	 * The default constructor.
	 *
	 * @param material the material the block is composed of.
	 */
	protected BlockBaseTile(Material material, String internalName, boolean isMultiBlock) {
		super(material, internalName);
		this.isMultiBlock = isMultiBlock;
		this.tileName = Reference.MOD_ID.toLowerCase() + ":tileEntity." + internalName;
		TileEntity tile = this.createNewTileEntity(null, 0);
		assert(tile != null);
		GameRegistry.registerTileEntity(tile.getClass(), getTileName());
	}

	public final String getTileName() {
		return tileName;
	}

	@Override
	public abstract T createNewTileEntity(World worldIn, int meta);
	
	/**
	 * Sets the block's orientation based upon the direction the player is
	 * looking when the block is placed.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityBase) {
			TileEntityBase tile = (TileEntityBase) world.getTileEntity(pos);
			if (tile.isRotatable()) {
				int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
				tile.setOrientation(AgriForgeDirection.getCardinal(direction));
			}
			if (this.isMultiBlock && !world.isRemote) {
				IMultiBlockComponent component = (IMultiBlockComponent) world.getTileEntity(pos);
				component.getMultiBlockManager().onBlockPlaced(world, pos, component);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (this.isMultiBlock && !world.isRemote) {
			IMultiBlockComponent component = (IMultiBlockComponent) world.getTileEntity(pos);
			component.getMultiBlockManager().onBlockBroken(world, pos, component);
		}
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

}
