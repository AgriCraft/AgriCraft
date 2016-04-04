package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockGrate;
import com.infinityraider.agricraft.client.renderers.blocks.RenderBlockGrate;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityGrate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockGrate extends BlockCustomWood {

	public BlockGrate() {
		super("grate", false);
	}

	@Override
	protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
		return ItemBlockGrate.class;
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderBlockGrate getRenderer() {
		return new RenderBlockGrate();
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityGrate();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || !(tile instanceof TileEntityGrate)) {
			return true;
		}
		TileEntityGrate grate = (TileEntityGrate) tile;
		boolean front = grate.isPlayerInFront(player);
		if (player.isSneaking()) {
			if (grate.removeVines(front)) {
				spawnAsEntity(world, pos, new ItemStack(Blocks.vine, 1));
				return true;
			}
		} else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Item.getItemFromBlock(Blocks.vine)) {
			if (grate.addVines(front) && !player.capabilities.isCreativeMode) {
				player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize - 1;
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> items = super.getDrops(world, pos, state, fortune);
		TileEntity te = world.getTileEntity(pos);
		if (te != null && (te instanceof TileEntityGrate)) {
			TileEntityGrate grate = (TileEntityGrate) te;
			int stackSize = 0;
			stackSize = grate.hasVines(true) ? stackSize + 1 : stackSize;
			stackSize = grate.hasVines(false) ? stackSize + 1 : stackSize;
			if (stackSize > 0) {
				items.add(new ItemStack(Blocks.vine, stackSize));
			}
		}
		return items;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		final TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityGrate) {
			final TileEntityGrate tg = (TileEntityGrate) te;
			final double[] b = tg.getBlockBounds();
			this.minX = b[0];
			this.minY = b[1];
			this.minZ = b[2];
			this.maxX = b[3];
			this.maxY = b[4];
			this.maxZ = b[5];
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		final double[] b;
		final TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityGrate) {
			final TileEntityGrate tg = (TileEntityGrate) te;
			b = tg.getBlockBounds();
		} else {
			b = new double[]{0, 0, 0, 1, 1, 1};
		}
		return new AxisAlignedBB(b[0] + pos.getX(), b[1] + pos.getY(), b[2] + pos.getZ(), b[3] + pos.getX(), b[4] + pos.getY(), b[5] + pos.getZ());
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
		return getCollisionBoundingBox(worldIn, pos, null);
	}

}
