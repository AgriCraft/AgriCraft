package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public class BlockChannelValve extends BlockContainer {

    public BlockChannelValve() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setBlockBounds(4*Constants.unit, 0, 4*Constants.unit, 12*Constants.unit, 1, 12*Constants.unit);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            updatePowerStatus(world, x, y, z);
        }
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
            world.setBlockToAir(x, y, z);
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockChannelValve, 1);
            drop.setTagCompound(((TileEntityCustomWood) world.getTileEntity(x, y, z)).getMaterialTag());
            this.dropBlockAsItem(world, x, y, z, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityCustomWood te = (TileEntityCustomWood) world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockChannelValve, 1, world.getBlockMetadata(x, y, z));
        NBTTagCompound tag = te.getMaterialTag();
        stack.stackTagCompound = tag;
        return stack;
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
