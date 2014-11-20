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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;

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
                if(stack.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem fluidContainer = (IFluidContainerItem) stack.getItem();
                    if (fluidContainer.getFluid(stack).getFluid() == FluidRegistry.WATER) {
                        int filledAmount = tank.fill(ForgeDirection.UNKNOWN, fluidContainer.getFluid(stack), false);
                        if (filledAmount == fluidContainer.getFluid(stack).amount) {
                            tank.fill(ForgeDirection.UNKNOWN, fluidContainer.getFluid(stack), true);
                            fluidContainer.drain(stack, filledAmount, true);
                            update = true;
                        }
                    }
                }
                else if(stack.getItem()==Items.water_bucket) {
                    int filledAmount = tank.fill(ForgeDirection.UNKNOWN, Constants.mB, false);
                    if(filledAmount == Constants.mB) {
                        tank.fill(ForgeDirection.UNKNOWN, Constants.mB, true);
                        if(!player.capabilities.isCreativeMode) {
                            stack.stackSize = 0;
                        }
                        update = true;
                    }
                }
                else if(stack.getItem()==Items.bucket) {
                    if(tank.getFluidLevel()>=Constants.mB) {
                        tank.setFluidLevel(tank.getFluidLevel()-Constants.mB);
                        ItemStack bucket = new ItemStack(Items.water_bucket, 1);
                        if(!player.inventory.addItemStackToInventory(bucket)) {
                            player.dropPlayerItemWithRandomChoice(new ItemStack(Items.water_bucket, 1), false);
                        }
                        if(!player.capabilities.isCreativeMode) {
                            stack.stackSize = stack.stackSize-1;
                        }
                        update = true;
                    }
                }
                else if(stack.getItem()==Items.glass_bottle) {

                }
            }
            if(update) {
                tank.markDirty();
            }
        }
        return false;
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
            world.setBlock(x, y, z, Blocks.water);
        }
        else {
            world.setBlockToAir(x, y, z);
        }
        world.notifyBlockOfNeighborChange(x, y, z, null);
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
}
