package com.infinityraider.agricraft.blocks.decoration;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockFenceGate;
import com.infinityraider.agricraft.blocks.tiles.decoration.TileEntityFenceGate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFenceGate extends BlockCustomWood<TileEntityFenceGate> {

	public BlockFenceGate() {
		super("fence_gate");
	}
	
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		world.playSound(player, pos, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.AMBIENT, 1003, 0);
        if(world.isRemote) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return true;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        if(gate.isOpen()) {
            gate.close();
        } else {
            if(gate.isZAxis()) {
                gate.open(player.posZ>=pos.getZ()+hitZ);
            } else {
                gate.open(player.posX>=pos.getX()+hitX);
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockFenceGate getRenderer() {
        return new RenderBlockFenceGate(this);
    }

    @Override
    public TileEntityFenceGate createNewTileEntity(World world, int meta) {
        return new TileEntityFenceGate();
    }

    @Override
    public Class<? extends ItemBlockCustomWood> getItemBlockClass() {
        return ItemBlockFenceGate.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        return getSelectedBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return null;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        return gate.isOpen()?null:(!gate.isZAxis() ?
                new AxisAlignedBB((double)((float) pos.getX() + 0.375F), (double) pos.getY(), (double) pos.getZ(), (double)((float) pos.getX() + 0.625F), (double)((float) pos.getY() + 1.5F), (double)(pos.getZ() + 1))
                : new AxisAlignedBB((double) pos.getX(), (double) pos.getY(), (double)((float) pos.getZ() + 0.375F), (double)(pos.getX() + 1), (double)((float) pos.getY() + 1.5F), (double)((float) pos.getZ() + 0.625F)));
    }

    public boolean isFenceGateOpen(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return !(tile == null || !(tile instanceof TileEntityFenceGate)) && ((TileEntityFenceGate) tile).isOpen();
    }

    public static class ItemBlockFenceGate extends ItemBlockCustomWood {
        public ItemBlockFenceGate(Block block) {
            super(block);
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
            if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
                EnumFacing dir = player.getHorizontalFacing();
                TileEntityFenceGate gate = (TileEntityFenceGate) world.getTileEntity(pos);
                gate.setZAxis(dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH);
                return true;
            } else {
                return false;
            }
        }
    }
	
	@Override
	public boolean isEnabled() {
		return AgriCraftConfig.enableFences;
	}

}
