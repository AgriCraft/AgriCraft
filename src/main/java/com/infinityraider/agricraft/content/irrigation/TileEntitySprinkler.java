package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySprinkler extends TileEntityBase implements ITickableTileEntity {
    private TileEntityIrrigationChannel channel;

    public TileEntitySprinkler() {
        super(AgriCraft.instance.getModTileRegistry().sprinkler);
    }

    @Override
    public void tick() {

    }

    @Nullable
    public TileEntityIrrigationChannel getChannel() {
        if(this.channel == null) {
            if(this.getWorld() == null) {
                return null;
            }
            TileEntity tile = this.getWorld().getTileEntity(this.getPos().up());
            if(tile instanceof TileEntityIrrigationChannel) {
                this.channel = (TileEntityIrrigationChannel) tile;
            }
        }
        return this.channel;
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }
}
