package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
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
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTank();
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, 1);
            drop.setTagCompound(((TileEntityCustomWood) world.getTileEntity(x, y, z)).getMaterialTag());
            this.dropBlockAsItem(world, x, y, z, drop);
        }
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
            world.removeTileEntity(x,y,z);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityCustomWood te = (TileEntityCustomWood) world.getTileEntity(x, y, z);
        ItemStack stack = new ItemStack(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterTank, 1, world.getBlockMetadata(x, y, z));
        NBTTagCompound tag = te.getMaterialTag();
        stack.stackTagCompound = tag;
        return stack;
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
        if(ConfigurationHandler.placeWater && placeWater) {
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
