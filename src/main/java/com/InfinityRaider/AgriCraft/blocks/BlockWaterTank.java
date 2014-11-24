package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BlockWaterTank extends BlockContainer{

    public BlockWaterTank() {
        super(Material.wood);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTank();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        boolean update=false;
        if(!world.isRemote) {
            TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
            ItemStack stack = player.getCurrentEquippedItem();
            if(stack!=null && stack.getItem()!=null) {
                FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
                //put water from liquid container in tank
                if(liquid!=null && liquid.getFluid()==FluidRegistry.WATER) {
                    int quantity = tank.fill(ForgeDirection.UNKNOWN, liquid, false);
                    if(quantity==liquid.amount) {
                        tank.fill(ForgeDirection.UNKNOWN, liquid, true);
                        update = true;
                        //change the inventory if player is not in creative mode
                        if(!player.capabilities.isCreativeMode) {
                            if(stack.stackSize==1) {
                                if (stack.getItem().hasContainerItem(stack)) {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, stack.getItem().getContainerItem(stack));
                                }
                                else {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                }
                            }
                            else {
                                stack.splitStack(1);
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                            }
                        }
                    }
                }
                //put water from tank in empty liquid container
                else {
                    FluidStack tankContents = tank.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
                    if(tankContents!=null) {
                        ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(tankContents, stack);
                        FluidStack filledLiquid = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
                        if(filledLiquid!=null) {
                            //change the inventory if the player is not in creative mode
                            if(!player.capabilities.isCreativeMode) {
                                if (stack.stackSize == 1) {
                                    if (stack.getItem().hasContainerItem(stack)) {
                                        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack.getItem().getContainerItem(stack));
                                    } else {
                                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                    }
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
                                } else {
                                    if (!player.inventory.addItemStackToInventory(filledContainer)) {
                                        return false;
                                    } else {
                                        stack.splitStack(1);
                                        player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                                        player.inventory.addItemStackToInventory(filledContainer);
                                        player.inventory.markDirty();
                                    }
                                }
                            }
                            tank.drain(ForgeDirection.UNKNOWN, filledLiquid.amount, true);
                            update = true;
                        }
                    }
                }
            }
            if(update) {
                tank.markDirty();
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    //when the block is broken
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        boolean placeWater = false;
        if(world.getTileEntity(x, y , z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityTank) {
            TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y ,z);
            tank.breakMultiBlock();
            placeWater = tank.getFluidLevel()>Constants.mB;
        }
        world.removeTileEntity(x,y,z);
        if(placeWater) {
            world.setBlock(x, y, z, Blocks.water, 0, 3);
        }
        else {
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));    //wooden tank
        list.add(new ItemStack(item, 1, 1));    //iron tank
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }


    //render methods
    //--------------
    @Override
    public int getRenderType() {return -1;}                 //get default render type: net.minecraft.client.renderer
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return false;}

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
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world, x, y , z, id, data);
        if(world.getTileEntity(x, y, z)!=null) {
            return (world.getTileEntity(x, y, z)).receiveClientEvent(id, data);
        }
        return false;
    }
}
