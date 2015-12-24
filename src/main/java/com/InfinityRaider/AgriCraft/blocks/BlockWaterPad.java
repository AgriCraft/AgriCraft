package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderWaterPad;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockWaterPad extends BlockBase {
    public BlockWaterPad() {
        this(Material.ground);
    }

    protected BlockWaterPad(Material mat) {
        super(mat);
        this.setHardness(0.5F);
        this.setStepSound(soundTypeGravel);
        this.maxY = Constants.UNIT * (Constants.WHOLE / 2);
    }

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderWaterPad(this);
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockWaterPad.class;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.waterPad;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fX, float fY, float fZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (FluidContainerRegistry.isContainer(stack)) {
            FluidStack waterBucket = new FluidStack(FluidRegistry.WATER, 1000);
            if (!FluidContainerRegistry.containsFluid(stack, waterBucket)) {
                return false;
            }
            if (!player.capabilities.isCreativeMode) {
                player.inventory.addItemStackToInventory(FluidContainerRegistry.drainFluidContainer(stack));
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize - 1;
            }
            if (!world.isRemote) {
                world.setBlockState(pos, this.getDefaultState(), 3);
            }
            return true;

        }
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(Blocks.dirt, 1);
            spawnAsEntity(world, pos, drop);
        }
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Blocks.dirt);
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube() {return false;}

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {return true;}

    public static class ItemBlockWaterPad extends net.minecraft.item.ItemBlock {
        public ItemBlockWaterPad(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadDry"));
        }
    }
}
