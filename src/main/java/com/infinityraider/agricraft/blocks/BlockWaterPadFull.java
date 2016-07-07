package com.infinityraider.agricraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockWaterPadFull extends AbstractBlockWaterPad {
	
    public BlockWaterPadFull() {
        super(Material.WATER, "full");
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
        if (FluidContainerRegistry.isContainer(stack)) {
            FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
            if (!FluidContainerRegistry.isEmptyContainer(stack)) {
                return false;
            }
            if (!world.isRemote) {
                if (!player.capabilities.isCreativeMode) {
                    ItemStack copy = stack.copy();
                    player.getActiveItemStack().stackSize = player.getActiveItemStack().stackSize - 1;
                    if (player.getActiveItemStack().stackSize == 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, FluidContainerRegistry.fillFluidContainer(waterBucket, copy));
                    } else if (!player.inventory.addItemStackToInventory(FluidContainerRegistry.fillFluidContainer(waterBucket, copy))) {
                        if (world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots) {
                            float f = 0.7F;
                            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                            EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, FluidContainerRegistry.fillFluidContainer(waterBucket, copy));
                            entityitem.setPickupDelay(10);
                            world.spawnEntityInWorld(entityitem);
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
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockWaterPadFull.class;
    }

    public static class ItemBlockWaterPadFull extends ItemBlockWaterPad {
        public ItemBlockWaterPadFull(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(AgriCore.getTranslator().translate("agricraft_tooltip.waterPadWet"));
        }
    }
}
