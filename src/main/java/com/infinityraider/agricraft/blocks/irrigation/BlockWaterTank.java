package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import com.infinityraider.agricraft.renderers.blocks.RenderTank;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;

import java.util.Optional;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterTank extends BlockCustomWood<TileEntityTank> {

    @SuppressWarnings("unchecked")
    public static final InfinityProperty<TileEntityTank.Connection>[] CONNECTION_PROPERTIES = new InfinityProperty[]{
        AgriProperties.TANK_DOWN,
        AgriProperties.TANK_UP,
        AgriProperties.TANK_NORTH,
        AgriProperties.TANK_SOUTH,
        AgriProperties.TANK_WEST,
        AgriProperties.TANK_EAST
    };

    public BlockWaterTank() {
        super("water_tank");
    }

    @Override
    public TileEntityTank createNewTileEntity(World world, int meta) {
        return new TileEntityTank();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean update = false;
        if (world.isRemote) {
            return true;
        }
        TileEntityTank tank = WorldHelper.getTile(world, pos, TileEntityTank.class).orElse(null);
        if (stack != null && stack.getItem() != null && tank != null) {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
            // put water from liquid container in tank
            if (liquid != null && liquid.getFluid() == FluidRegistry.WATER) {
                int quantity = tank.fill(null, liquid, false);
                if (quantity == liquid.amount) {
                    tank.fill(null, liquid, true);
                    update = true;
                    // change the inventory if player is not in creative mode
                    if (!player.capabilities.isCreativeMode) {
                        if (stack.stackSize == 1) {
                            if (stack.getItem().hasContainerItem(stack)) {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack.getItem().getContainerItem(stack));
                            } else {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                            }
                        } else {
                            stack.splitStack(1);
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                        }
                    }
                }
            } // put water from tank in empty liquid container
            else {
                FluidStack tankContents = tank.getTankInfo(null)[0].fluid;
                if (tankContents != null) {
                    ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(tankContents, stack);
                    FluidStack filledLiquid = FluidContainerRegistry.getFluidForFilledItem(filledContainer);
                    if (filledLiquid != null) {
                        // change the inventory if the player is not in creative mode
                        if (!player.capabilities.isCreativeMode) {
                            if (stack.stackSize == 1) {
                                if (stack.getItem().hasContainerItem(stack)) {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, stack.getItem().getContainerItem(stack));
                                } else {
                                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                }
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
                            } else if (!player.inventory.addItemStackToInventory(filledContainer)) {
                                return false;
                            } else {
                                stack.splitStack(1);
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
                                player.inventory.addItemStackToInventory(filledContainer);
                                player.inventory.markDirty();
                            }
                        }
                        tank.drain(null, filledLiquid.amount, true);
                        update = true;
                    }
                }
            }
        }
        return update;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderTank getRenderer() {
        return new RenderTank(this);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return CONNECTION_PROPERTIES;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityTank> tile = WorldHelper.getTile(worldIn, pos, TileEntityTank.class);
        if (tile.isPresent()) {
            TileEntityTank tank = tile.get();
            for (EnumFacing facing : EnumFacing.values()) {
                state = CONNECTION_PROPERTIES[facing.ordinal()].applyToBlockState(state, tank.getConnectionType(facing));
            }
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
