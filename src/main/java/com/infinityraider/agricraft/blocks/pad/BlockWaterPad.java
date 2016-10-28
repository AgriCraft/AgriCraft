package com.infinityraider.agricraft.blocks.pad;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;

public class BlockWaterPad extends AbstractBlockWaterPad {

    public BlockWaterPad() {
        this(Material.GROUND);
    }

    protected BlockWaterPad(Material mat) {
        super(mat, "normal");
    }

    @Override
    public List<String> getOreTags() {
        return Collections.emptyList();
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[0];
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockWaterPad.class;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (FluidContainerRegistry.isContainer(stack)) { FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
            if (!FluidContainerRegistry.containsFluid(stack, waterBucket)) {
                return false;
            }
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) {
                    ItemStack copy = stack.copy();
                    player.getActiveItemStack().stackSize = player.getActiveItemStack().stackSize - 1;
                    if(player.getActiveItemStack().stackSize == 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.drainFluidContainer(copy));
                    } else {
                        ItemStack drained = FluidContainerRegistry.drainFluidContainer(copy);
                        if (!player.inventory.addItemStackToInventory(FluidContainerRegistry.fillFluidContainer(waterBucket, copy))) {
                            if (!player.inventory.addItemStackToInventory(drained)) {
                                if (world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
                                    float f = 0.7F;
                                    double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                    double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                    double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                                    EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, drained);
                                    entityitem.setPickupDelay(10);
                                    world.spawnEntityInWorld(entityitem);
                                }
                            }
                        }
                    }
                }
                world.setBlockState(pos, this.getDefaultState(), 3);
            }
            return true;

        }
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(Blocks.DIRT, 1);
            spawnAsEntity(world, pos, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Blocks.DIRT);
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube(IBlockState state) {return false;}

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {return true;}
}
