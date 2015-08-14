package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockFence;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFence;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFence extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.fence;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockFence();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.fence;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityFence();
    }

    public boolean canConnect(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile==null || !(tile instanceof TileEntityFence)) {
            Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (block == null) {
                return false;
            }
            if (block.isAir(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                return false;
            }
            return true;
        }
        return ((TileEntityFence) tile).canConnect(dir);
    }
}
