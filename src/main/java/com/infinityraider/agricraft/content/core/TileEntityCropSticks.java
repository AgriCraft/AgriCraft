package com.infinityraider.agricraft.content.core;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class TileEntityCropSticks extends TileEntityCropBase {
    // Auto synced fields
    private final AutoSyncedField<Boolean> crossCrop;
    // Cache for neighbouring crops
    private final Map<Direction, Optional<IAgriCrop>> neighbours;
    private boolean needsCaching;

    public TileEntityCropSticks() {
        // Super constructor with appropriate TileEntity Type
        super(AgriCraft.instance.getModTileRegistry().crop_sticks);

        // Initialize automatically synced fields
        this.crossCrop = this.getAutoSyncedFieldBuilder(false)
                .withCallBack(status -> {
                    if (this.getWorld() != null) {
                        this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.CROSS_CROP.apply(this.getBlockState(), status));
                    }})
                .withRenderUpdate()
                .build();

        // Initialize neighbour cache
        this.neighbours = Maps.newEnumMap(Direction.class);
        Direction.Plane.HORIZONTAL.getDirectionValues().forEach(dir -> neighbours.put(dir, Optional.empty()));
        this.needsCaching = true;
    }

    @Override
    public boolean hasCropSticks() {
        return true;
    }

    @Override
    public boolean isCrossCrop() {
        return this.crossCrop.get();
    }

    @Override
    public boolean setCrossCrop(boolean status) {
        if(this.hasPlant()) {
            return false;
        }
        if(this.hasWeeds()) {
            return false;
        }
        if(this.isCrossCrop() == status) {
            return false;
        }
        this.crossCrop.set(status);
        return true;
    }

    // Use neighbour cache instead to prevent having to read TileEntities from the world
    @Nonnull
    @Override
    public Stream<IAgriCrop> streamNeighbours() {
        if(this.needsCaching) {
            this.readNeighbours();
        }
        return this.neighbours.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(IAgriCrop::hasCropSticks);
    }

    // Initialize neighbours cache
    protected void readNeighbours() {
        if(this.getWorld() != null) {
            Direction.Plane.HORIZONTAL.getDirectionValues().forEach(dir -> neighbours.put(dir, AgriApi.getCrop(this.getWorld(), this.getPos().offset(dir))));
            this.needsCaching = false;
        }
    }

    // Update neighbour cache
    protected void onNeighbourChange(Direction direction, BlockPos pos, BlockState newState) {
        if(newState.getBlock() instanceof BlockCropSticks) {
            if(this.getWorld() != null) {
                this.neighbours.put(direction, AgriApi.getCrop(this.getWorld(), pos));
            }
        } else {
            this.neighbours.put(direction, Optional.empty());
        }
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        // A cache update will be required though (either on the client, or on the server after being loaded)
        this.needsCaching = true;
    }
}
