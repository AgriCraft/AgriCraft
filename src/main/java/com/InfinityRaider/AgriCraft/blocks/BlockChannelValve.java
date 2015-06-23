package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class BlockChannelValve extends BlockCustomWood {

    public BlockChannelValve() {
        super();
        this.setBlockBounds(4*Constants.unit, 0, 4*Constants.unit, 12*Constants.unit, 1, 12*Constants.unit);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            updatePowerStatus(world, x, y, z);
            if(block instanceof BlockLever) {
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {
        if (!world.isRemote) {
            updatePowerStatus(world, x, y, z);
        }
    }

    private void updatePowerStatus(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te !=null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            valve.updatePowerStatus();
        }
    }

    //allows levers to be attached to the block
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side!=ForgeDirection.UP;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityValve();
    }

    @Override
    public int getRenderType() {
        return AgriCraft.proxy.getRenderId(Constants.valveId);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(0, 0);
    }
}
