package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class BlockIrrigationChannelAbstract extends BlockDynamicTexture<TileEntityChannel> {
    public BlockIrrigationChannelAbstract(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityChannel tile, LootContext.Builder context) {

    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable TileEntity tile) {

    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return null;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityChannel> getTileEntityFactory() {
        return null;
    }
}
