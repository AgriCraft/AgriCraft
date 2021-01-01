package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class CropEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final IAgriCrop crop;

    protected CropEvent(World world, BlockPos pos, @Nonnull IAgriCrop crop) {
        this.world = world;
        this.pos = pos;
        this.crop = Objects.requireNonNull(crop);;
    }

    @Nonnull
    public IAgriCrop getCrop() {
        return crop;
    }

    @Cancelable
    public static class Harvest extends CropEvent {
        private final PlayerEntity player;

        public Harvest(World world, BlockPos pos, @Nonnull IAgriCrop crop, @Nullable PlayerEntity player) {
            super(world, pos, crop);
            this.player = player;
        }

        @Nullable
        public PlayerEntity getPlayer() {
            return player;
        }
    }

    @Cancelable
    public static class Grow extends CropEvent {
        public Grow(World world, BlockPos pos, @Nonnull IAgriCrop crop) {
            super(world, pos, crop);
        }
    }

    public static class Plant extends CropEvent {
        private final AgriSeed seed;
        private final PlayerEntity player;

        public Plant(World world, BlockPos pos, @Nonnull IAgriCrop crop, @Nonnull AgriSeed seed, @Nullable PlayerEntity player) {
            super(world, pos, crop);
            this.seed = Objects.requireNonNull(seed);
            this.player = player;
        }

        @Nonnull
        public AgriSeed getSeed() {
            return seed;
        }

        @Nullable
        public PlayerEntity getPlayer() {
            return player;
        }

    }
}
