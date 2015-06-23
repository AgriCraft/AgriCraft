package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class BlockWaterChannel extends BlockCustomWood {
    public BlockWaterChannel() {
        super();
        this.setBlockBounds(4*Constants.unit, 4*Constants.unit, 4*Constants.unit, 12*Constants.unit, 12*Constants.unit, 12*Constants.unit);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        return world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z).receiveClientEvent(id, data);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        //adjacent boxes
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) te;
            float f = Constants.unit;   //one 16th of a block
            float min = 4 * f;
            float max = 12 * f;
            if (channel.hasNeighbour('x', 1)) {
                this.setBlockBounds(max - f, min, min, f * 16, max, max);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbour('x', -1)) {
                this.setBlockBounds(0, min, min, min + f, max, max);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbour('z', 1)) {
                this.setBlockBounds(min, min, max - f, max, max, f * 16);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbour('z', -1)) {
                this.setBlockBounds(min, min, 0, max, max, min + f);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            //central box
            this.setBlockBounds(min, min, min, max, max, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityChannel channel = (TileEntityChannel) world.getTileEntity(x, y, z);
        float f = Constants.unit;
        AxisAlignedBB minBB = AxisAlignedBB.getBoundingBox(4 * f, 4 * f, 4 * f, 12 * f, 12 * f, 12 * f);
        float min = 4 * f;
        float max = 12 * f;
        if (channel.hasNeighbour('x', 1)) {
            minBB.setBounds(minBB.minX, min, minBB.minZ, 1, max, minBB.maxZ);
        }
        if (channel.hasNeighbour('x', -1)) {
            minBB.setBounds(0, min, minBB.minZ, minBB.maxX, max, minBB.maxZ);
        }
        if (channel.hasNeighbour('z', 1)) {
            minBB.setBounds(minBB.minX, min, minBB.minZ, minBB.maxX, max, 1);
        }
        if (channel.hasNeighbour('z', -1)) {
            minBB.setBounds(minBB.minX, min, 0, minBB.maxX, max, minBB.maxZ);
        }
        return minBB.getOffsetBoundingBox(x, y, z);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));    //wooden channel
        list.add(new ItemStack(item, 1, 1));    //iron pipe
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    //render methods
    //--------------
    @Override
    public int getRenderType() {return AgriCraft.proxy.getRenderId(Constants.channelId);}
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(meta==0) {
            return Blocks.planks.getIcon(0, 0);
        }
        else if(meta==1) {
            return Blocks.iron_block.getIcon(0, 0);
        }
        return null;
    }
}
