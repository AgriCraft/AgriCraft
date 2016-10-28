package com.infinityraider.agricraft.compat.computer.methods;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;

public interface IMethod {

    String getName();

    String getDescription();

    String getSignature();

    Object[] call(TileEntityPeripheral peripheral, World world, BlockPos pos, ItemStack journal, Object... args) throws MethodException;

}
