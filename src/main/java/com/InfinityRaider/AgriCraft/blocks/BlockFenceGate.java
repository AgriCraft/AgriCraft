package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFenceGate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFenceGate;
import com.InfinityRaider.AgriCraft.utility.PlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFenceGate extends BlockCustomWood {
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        world.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
        if(world.isRemote) {
            return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return true;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        if(gate.isOpen()) {
            gate.close();
        } else {
            if(gate.isZAxis()) {
                gate.open(player.posZ>=z+fZ);
            } else {
                gate.open(player.posX>=x+fX);
            }
        }
        return true;
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.fenceGate;
    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderBlockFenceGate();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.fenceGate;
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile==null || !(tile instanceof TileEntityFenceGate)) {
            return null;
        }
        TileEntityFenceGate gate = (TileEntityFenceGate) tile;
        return gate.isOpen()?null:(!gate.isZAxis()?AxisAlignedBB.getBoundingBox((double)((float)x + 0.375F), (double)y, (double)z, (double)((float)x + 0.625F), (double)((float)y + 1.5F), (double)(z + 1)) : AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)((float)z + 0.375F), (double)(x + 1), (double)((float)y + 1.5F), (double)((float)z + 0.625F)));
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
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

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z)  {
        return isFenceGateOpen(world, x, y, z);
    }

    public boolean isFenceGateOpen(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return !(tile == null || !(tile instanceof TileEntityFenceGate)) && ((TileEntityFenceGate) tile).isOpen();
    }

    public static class ItemBlockFenceGate extends ItemBlockCustomWood {
        public ItemBlockFenceGate(Block block) {
            super(block);
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
            if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
                ForgeDirection dir = PlayerHelper.getPlayerFacing(player);
                TileEntityFenceGate gate = (TileEntityFenceGate) world.getTileEntity(x, y, z);
                gate.setZAxis(dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH);
                return true;
            } else {
                return false;
            }
        }
    }
}
