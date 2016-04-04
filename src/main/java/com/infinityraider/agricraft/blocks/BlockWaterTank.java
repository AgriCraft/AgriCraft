package com.infinityraider.agricraft.blocks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import com.infinityraider.agricraft.client.renderers.blocks.RenderTank;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterTank extends BlockCustomWood {

	public BlockWaterTank() {
		super("water_tank", true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTank();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fX, float fY, float fZ) {
		boolean update = false;
		if (world.isRemote) {
			return true;
		}
		TileEntityTank tank = (TileEntityTank) world.getTileEntity(pos);
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null && stack.getItem() != null) {
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
			}
			// put water from tank in empty liquid container
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
						tank.drain(null, filledLiquid.amount, true);
						update = true;
					}
				}
			}
		}
		return update;
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderTank getRenderer() {
		return new RenderTank();
	}

}
