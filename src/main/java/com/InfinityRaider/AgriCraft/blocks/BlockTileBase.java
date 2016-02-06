package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.utility.AgriForgeDirection;
import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The base class for all AgriCraft tile blocks.
 */
public abstract class BlockTileBase extends BlockBase implements ITileEntityProvider {
	
	public final boolean isMultiBlock;
	public final String tileName;

	/**
	 * The default constructor.
	 *
	 * @param material the material the block is composed of.
	 */
	protected BlockTileBase(Material material, String internalName, boolean isMultiBlock) {
		this(material, internalName, internalName, isMultiBlock);
	}
	
	protected BlockTileBase(Material material, String internalName, String tileName, boolean isMultiBlock) {
		super(material, internalName);
		this.tileName = tileName;
		this.isMultiBlock = isMultiBlock;
		TileEntity tile = this.createNewTileEntity(null, 0);
		assert(tile != null);
		GameRegistry.registerTileEntity(tile.getClass(), Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + this.tileName);
	}
	
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

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int data) {
		super.onBlockEventReceived(world, pos, state, id, data);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null && tileentity.receiveClientEvent(id, data);
	}

}
