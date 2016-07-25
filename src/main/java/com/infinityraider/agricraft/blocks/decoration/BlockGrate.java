package com.infinityraider.agricraft.blocks.decoration;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.items.blocks.ItemBlockGrate;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockGrate;
import com.infinityraider.agricraft.tiles.decoration.TileEntityGrate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.List;

public class BlockGrate extends BlockCustomWood<TileEntityGrate> {

	public BlockGrate() {
		super("grate");
		this.fullBlock = false;
	}

	@Override
	public Class<? extends ItemBlockCustomWood> getItemBlockClass() {
		return ItemBlockGrate.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderBlockGrate getRenderer() {
		return new RenderBlockGrate(this);
	}

	@Override
	public TileEntityGrate createNewTileEntity(World world, int meta) {
		return new TileEntityGrate();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null || !(tile instanceof TileEntityGrate)) {
			return true;
		}
		TileEntityGrate grate = (TileEntityGrate) tile;
		boolean front = grate.isPlayerInFront(player);
		if (player.isSneaking()) {
			if (grate.removeVines(front)) {
				spawnAsEntity(world, pos, new ItemStack(Blocks.VINE, 1));
				return true;
			}
		} else if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.VINE)) {
			if (grate.addVines(front) && !player.capabilities.isCreativeMode) {
				stack.stackSize = stack.stackSize - 1;
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
				items.add(new ItemStack(Blocks.VINE, stackSize));
			}
		}
		return items;
	}

	/*
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		return getSelectedBoundingBox(state, world, pos);
	}
	*/

	/*
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		final double[] b;
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityGrate) {
			final TileEntityGrate tg = (TileEntityGrate) te;
			b = tg.getBlockBounds();
		} else {
			b = new double[]{0, 0, 0, 1, 1, 1};
		}
		return new AxisAlignedBB(b[0] + pos.getX(), b[1] + pos.getY(), b[2] + pos.getZ(), b[3] + pos.getX(), b[4] + pos.getY(), b[5] + pos.getZ());
	}
	*/

	@Override
	public boolean isEnabled() {
		return AgriCraftConfig.enableGrates;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

}
