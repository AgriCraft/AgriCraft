package com.infinityraider.agricraft.api.v1.misc;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.compat.computer.methods.MethodException;
import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAgriPeripheralMethod extends IAgriRegisterable {

    String getDescription();

    String getSignature();

    Object[] call(TileEntityPeripheral peripheral, World world, BlockPos pos, ItemStack journal, Object... args) throws MethodException;

}
