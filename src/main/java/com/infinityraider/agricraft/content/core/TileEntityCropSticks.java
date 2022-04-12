package com.infinityraider.agricraft.content.core;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.content.AgriTileRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityCropSticks extends TileEntityCropBase {
    // Auto synced fields
    private final AutoSyncedField<Boolean> crossCrop;
    // Cache for neighbouring crops
    private final Map<Direction, Optional<IAgriCrop>> neighbours;
    private boolean needsCaching;

    public TileEntityCropSticks(BlockPos pos, BlockState state) {
        // Super constructor with appropriate TileEntity Type
        super(AgriTileRegistry.crop_sticks, pos, state);

        // Initialize automatically synced fields
        this.crossCrop = this.getAutoSyncedFieldBuilder(false)
                .withCallBack(status -> {
                    if (this.getLevel() != null) {
                        this.getLevel().setBlock(this.getPosition(), BlockCropSticks.CROSS_CROP.apply(this.getBlockState(), status), 3);
                    }})
                .withRenderUpdate()
                .build();

        // Initialize neighbour cache
        this.neighbours = Maps.newEnumMap(Direction.class);
        Direction.Plane.HORIZONTAL.stream().forEach(dir -> neighbours.put(dir, Optional.empty()));
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
        if(this.getLevel() != null) {
            Direction.Plane.HORIZONTAL.stream().forEach(dir -> neighbours.put(dir, AgriApi.getCrop(this.getLevel(), this.getBlockPos().relative(dir))));
            this.needsCaching = false;
        }
    }

    // Update neighbour cache
    protected void onNeighbourChange(Direction direction, BlockPos pos, BlockState newState) {
        if(newState.getBlock() instanceof BlockCropSticks) {
            if(this.getLevel() != null) {
                this.neighbours.put(direction, AgriApi.getCrop(this.getLevel(), pos));
            }
        } else {
            this.neighbours.put(direction, Optional.empty());
        }
    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {
        // A cache update will be required though (either on the client, or on the server after being loaded)
        this.needsCaching = true;
    }
}
