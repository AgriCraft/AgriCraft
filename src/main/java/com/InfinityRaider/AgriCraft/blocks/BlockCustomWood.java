package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockCustomWood extends BlockContainer {
    public BlockCustomWood() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
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

    protected void setTag(World world, int x, int y, int z, ItemStack stack) {
        if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityCustomWood) {
            TileEntityCustomWood te = (TileEntityCustomWood) world.getTileEntity(x, y, z);
            stack.stackTagCompound = te.getMaterialTag();
        }
    }
}
