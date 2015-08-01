package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockContainerAgriCraft extends BlockAgriCraft implements ITileEntityProvider {
    protected BlockContainerAgriCraft(Material material) {
        super(material);
    }

    //this sets the block's orientation based upon the direction the player is looking when the block is placed
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te!=null && te instanceof TileEntityAgricraft) {
            TileEntityAgricraft tile = (TileEntityAgricraft) world.getTileEntity(x, y, z);
            if(tile.isRotatable()) {
                int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
                switch (direction) {
                    case 0:
                        tile.setOrientation(ForgeDirection.NORTH.ordinal());
                        break;
                    case 1:
                        tile.setOrientation(ForgeDirection.EAST);
                        break;
                    case 2:
                        tile.setOrientation(ForgeDirection.SOUTH);
                        break;
                    case 3:
                        tile.setOrientation(ForgeDirection.WEST);
                        break;
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
        super.breakBlock(world, x, y, z, b, meta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world, x, y, z, id, data);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity!=null && tileentity.receiveClientEvent(id, data);
    }
}
