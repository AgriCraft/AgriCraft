package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class BlockWaterPadFull extends BlockWaterPad {
    public BlockWaterPadFull() {
        super(Material.water);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
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
                world.setBlock(x, y, z, Blocks.blockWaterPad, 0, 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    public static class ItemBlockWaterPadFull extends ItemBlockWaterPad {
        public ItemBlockWaterPadFull(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadWet"));
        }
    }
}
