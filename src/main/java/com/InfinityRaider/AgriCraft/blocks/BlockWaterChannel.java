package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

public class BlockWaterChannel extends BlockContainer {
    public BlockWaterChannel() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setBlockBounds(4*Constants.unit, 4*Constants.unit, 4*Constants.unit, 12*Constants.unit, 12*Constants.unit, 12*Constants.unit);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world,x,y,z,block,meta);
        world.removeTileEntity(x,y,z);
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    //when the block is harvested
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x,y,z), 0);
            }
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, 1);
            drop.setTagCompound(((TileEntityCustomWood) world.getTileEntity(x, y, z)).getMaterialTag());
            this.dropBlockAsItem(world, x, y, z, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityCustomWood te = (TileEntityCustomWood) world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel, 1, world.getBlockMetadata(x, y, z));
        NBTTagCompound tag = te.getMaterialTag();
        stack.stackTagCompound = tag;
        return stack;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        //adjacent boxes
        TileEntityChannel channel = (TileEntityChannel) world.getTileEntity(x, y, z);
        float f = Constants.unit;   //one 16th of a block
        float min = 4*f;
        float max = 12*f;
        if(channel.hasNeighbour('x', 1)) {
            this.setBlockBounds(max-f, min, min, f*16, max, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        if(channel.hasNeighbour('x', -1)) {
            this.setBlockBounds(0, min, min, min+f, max, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        if(channel.hasNeighbour('z', 1)) {
            this.setBlockBounds(min, min, max-f, max, max,  f*16);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        if(channel.hasNeighbour('z', -1)) {
            this.setBlockBounds(min, min, 0, max, max, min+f);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        //central box
        this.setBlockBounds(min, min, min, max, max, max);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
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
