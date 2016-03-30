package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.renderers.blocks.RenderBlockFenceGate;
import com.infinityraider.agricraft.tileentity.decoration.TileEntityFenceGate;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import com.infinityraider.agricraft.utility.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFenceGate extends BlockCustomWood {

	public BlockFenceGate() {
		super("fence_gate", false);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        world.playAuxSFXAtEntity(player, 1003, pos, 0);
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
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockFenceGate getRenderer() {
        return new RenderBlockFenceGate();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFenceGate();
    }

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
        return ItemBlockFenceGate.class;
    }

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return null;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        return gate.isOpen()?null:(!gate.isZAxis()?new AxisAlignedBB((double)((float) pos.getX() + 0.375F), (double) pos.getY(), (double) pos.getZ(), (double)((float) pos.getX() + 0.625F), (double)((float) pos.getY() + 1.5F), (double)(pos.getZ() + 1)) : new AxisAlignedBB((double) pos.getX(), (double) pos.getY(), (double)((float) pos.getZ() + 0.375F), (double)(pos.getX() + 1), (double)((float) pos.getY() + 1.5F), (double)((float) pos.getZ() + 0.625F)));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, pos
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        if (!gate.isZAxis()) {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
        else {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
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
                AgriForgeDirection dir = PlayerHelper.getPlayerFacing(player);
                TileEntityFenceGate gate = (TileEntityFenceGate) world.getTileEntity(pos);
                gate.setZAxis(dir == AgriForgeDirection.NORTH || dir == AgriForgeDirection.SOUTH);
                return true;
            } else {
                return false;
            }
        }
    }
}
