package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BlockWaterTank extends BlockCustomWood{

    public BlockWaterTank() {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTank();
    }

    //This gets called when the block is right clicked
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
                tank.syncToClient(false);
                world.markBlockForUpdate(x, y, z);
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
        if(!world.isRemote) {
            LogHelper.debug("breaking tank");
            boolean placeWater = false;
            LogHelper.debug("TileEntity found: " + (world.getTileEntity(x, y, z) != null));
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityTank) {
                TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                if(tank!=null) {
                    tank.breakMultiBlock(true, tank.getFluidLevel());
                    placeWater = tank.getFluidLevel() >= Constants.mB;
                }
            }
            world.removeTileEntity(x, y, z);
            if (ConfigurationHandler.placeWater && placeWater) {
                world.setBlock(x, y, z, Blocks.water, 0, 3);
                Blocks.water.onNeighborBlockChange(world, x, y, z, null);
            } else {
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityTank) {
                TileEntityTank tank = (TileEntityTank) te;
                if(block instanceof BlockWaterTank) {
                    tank.breakMultiBlock(true, tank.getFluidLevel());
                }
                tank.updateMultiBlock();
            }
        }
    }


    //render methods
    //--------------
    @Override
    public int getRenderType() {return AgriCraft.proxy.getRenderId(Constants.tankId);}                 //get the correct renderId
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return true;}

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
