package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockWaterChannel extends BlockCustomWood {
    
    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;
    
    public BlockWaterChannel() {
        super();
        this.setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.channel;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityChannel) {
            ((TileEntityChannel) te).findNeighbours();
        }
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        return world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z).receiveClientEvent(id, data);
    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        //adjacent boxes
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityChannel) {
            TileEntityChannel channel = (TileEntityChannel) te;
            if (channel.hasNeighbourCheck(ForgeDirection.EAST)) {
                this.setBlockBounds(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbourCheck(ForgeDirection.WEST)) {
                this.setBlockBounds(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbourCheck(ForgeDirection.SOUTH)) {
                this.setBlockBounds(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            if (channel.hasNeighbourCheck(ForgeDirection.NORTH)) {
                this.setBlockBounds(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT);
                super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            }
            //central box
            this.setBlockBounds(MIN, MIN, MIN, MAX, MAX, MAX);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityChannel channel = (TileEntityChannel) world.getTileEntity(x, y, z);
        AxisAlignedBB minBB = AxisAlignedBB.getBoundingBox(MIN, MIN, MIN, MAX, MAX, MAX);
        if (channel.hasNeighbourCheck(ForgeDirection.EAST)) {
            minBB.setBounds(minBB.minX, MIN, minBB.minZ, 1, MAX, minBB.maxZ);
        }
        if (channel.hasNeighbourCheck(ForgeDirection.WEST)) {
            minBB.setBounds(0, MIN, minBB.minZ, minBB.maxX, MAX, minBB.maxZ);
        }
        if (channel.hasNeighbourCheck(ForgeDirection.SOUTH)) {
            minBB.setBounds(minBB.minX, MIN, minBB.minZ, minBB.maxX, MAX, 1);
        }
        if (channel.hasNeighbourCheck(ForgeDirection.NORTH)) {
            minBB.setBounds(minBB.minX, MIN, 0, minBB.maxX, MAX, minBB.maxZ);
        }
        return minBB.getOffsetBoundingBox(x, y, z);
    }

    @Override
    @SuppressWarnings("unchecked")
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

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderChannel();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.channel;
    }
}
