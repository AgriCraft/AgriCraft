package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.BlockStatePlaceHolder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockWaterPadFull extends BlockWaterPad {
    public BlockWaterPadFull() {
        super(Material.water);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fX, float fY, float fZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (FluidContainerRegistry.isContainer(stack)) {
            FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
            if (!FluidContainerRegistry.isEmptyContainer(stack)) {
                return false;
            }
            if (!player.capabilities.isCreativeMode) {
                player.inventory.addItemStackToInventory(FluidContainerRegistry.fillFluidContainer(waterBucket, stack));
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize - 1;
            }
            if (!world.isRemote) {
                world.setBlockState(pos, new BlockStatePlaceHolder(Blocks.blockWaterPad, 0), 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos) {
        return false;
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockWaterPadFull.class;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.waterPadFull;
    }

    public static class ItemBlockWaterPadFull extends ItemBlockWaterPad {
        public ItemBlockWaterPadFull(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadWet"));
        }
    }
}
