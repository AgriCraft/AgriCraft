package com.InfinityRaider.AgriCraft.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWaterTank extends BlockCustomWood {

	public BlockWaterTank() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTank();
	}

	// This gets called when the block is right clicked
	// This is a terrible mess...
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
		boolean update = false;
		if (world.isRemote) {
			return true;
		}
		TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
		ItemStack stack = player.getCurrentEquippedItem();
		if (stack != null && stack.getItem() != null) {
			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
			// put water from liquid container in tank
			if (liquid != null && liquid.getFluid() == FluidRegistry.WATER) {
				int quantity = tank.fill(ForgeDirection.UNKNOWN, liquid, false);
				if (quantity == liquid.amount) {
					tank.fill(ForgeDirection.UNKNOWN, liquid, true);
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
				FluidStack tankContents = tank.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
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
						tank.drain(ForgeDirection.UNKNOWN, filledLiquid.amount, true);
						update = true;
					}
				}
			}
		}
		return update;
	}

	@Override
	public boolean isMultiBlock() {
		return true;
	}

	@Override
	protected String getTileEntityName() {
		return Names.Objects.tank;
	}

	// Sorry about the water placing thing... I'll find a way to add that back in later.

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderBlockBase getRenderer() {
		return new RenderTank();
	}

	@Override
	protected String getInternalName() {
		return Names.Objects.tank;
	}

}
