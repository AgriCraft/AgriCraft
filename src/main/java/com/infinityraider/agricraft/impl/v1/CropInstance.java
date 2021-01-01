package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.blocks.core.BlockCropSticks;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class CropInstance implements IAgriCrop {

    public static Optional<IAgriCrop> get(World world, BlockPos pos) {
        return get(world.getBlockState(pos), world, pos);
    }

    public static Optional<IAgriCrop> get(BlockState state, World world, BlockPos pos) {
        if(state.getBlock() instanceof IAgriCrop) {
            return Optional.of(new CropInstance(state, world, pos));
        } else {
            return Optional.empty();
        }
    }

    // Wrapped crop
    private WrappedCrop wrapped;

    private CropInstance(BlockState state, World world, BlockPos pos) {
        this.wrapped = WrappedCrop.get(state, world, pos, this::onStateChanged);
    }

    private void onStateChanged(BlockState state) {
        if(this.isValid()) {
            if(!(state.getBlock() instanceof BlockCropSticks)) {
                // Validity has changed
                this.wrapped = WrappedCrop.get(state, this.getWorld(), this.getPosition(), this::onStateChanged);
            }
        } else {
            if(state.getBlock() instanceof BlockCropSticks) {
                // Validity has changed
                this.wrapped = WrappedCrop.get(state, this.getWorld(), this.getPosition(), this::onStateChanged);
            }
        }
    }

    @Override
    public boolean isValid() {
        return this.wrapped.isValid();
    }

    @Override
    public World getWorld() {
        return this.wrapped.getWorld();
    }

    @Override
    public BlockPos getPosition() {
        return this.wrapped.getPosition();
    }

    @Override
    public IAgriGrowthStage getGrowthStage() {
        return this.wrapped.getGrowthStage();
    }

    @Override
    public boolean setGrowthStage(IAgriGrowthStage stage) {
        return this.wrapped.setGrowthStage(stage);
    }

    @Override
    public boolean isCrossCrop() {
        return this.wrapped.isCrossCrop();
    }

    @Override
    public boolean setCrossCrop(boolean status) {
        return this.wrapped.setCrossCrop(status);
    }

    @Override
    public boolean isFertile() {
        return this.wrapped.isFertile();
    }

    @Override
    public boolean isMature() {
        return this.wrapped.isMature();
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil() {
        return this.wrapped.getSoil();
    }

    @Override
    public void breakCrop() {
        this.wrapped.breakCrop();
    }

    @Override
    public void applyGrowthTick() {
        this.wrapped.applyGrowthTick();
    }

    @Override
    public boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer) {
        return this.wrapped.acceptsFertilizer(fertilizer);
    }

    @Nonnull
    @Override
    public ActionResultType onApplyFertilizer(IAgriFertilizer fertilizer, @Nonnull Random rand) {
        return this.wrapped.onApplyFertilizer(fertilizer, rand);
    }

    @Override
    public boolean canBeHarvested() {
        return this.wrapped.canBeHarvested();
    }

    @Nonnull
    @Override
    public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
        return wrapped.harvest(consumer, player);
    }

    @Override
    public boolean canBeRaked() {
        return this.wrapped.canBeRaked();
    }

    @Override
    public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
        return this.wrapped.rake(consumer, player);
    }

    @Override
    public boolean acceptsPlant(@Nullable IAgriPlant plant) {
        return this.wrapped.acceptsPlant(plant);
    }

    @Override
    public boolean setPlant(@Nullable IAgriPlant plant) {
        return this.wrapped.setPlant(plant);
    }

    @Nonnull
    @Override
    public Optional<IAgriPlant> removePlant() {
        return this.wrapped.removePlant();
    }

    @Override
    public boolean hasPlant() {
        return this.wrapped.hasPlant();
    }

    @Nonnull
    @Override
    public Optional<IAgriPlant> getPlant() {
        return this.wrapped.getPlant();
    }

    @Override
    public boolean acceptsSeed(@Nullable AgriSeed seed) {
        return this.wrapped.acceptsSeed(seed);
    }

    @Override
    public boolean setSeed(@Nullable AgriSeed seed) {
        return this.wrapped.setSeed(seed);
    }

    @Override
    public boolean hasSeed() {
        return this.wrapped.hasSeed();
    }

    @Override
    public Optional<AgriSeed> getSeed() {
        return this.wrapped.getSeed();
    }

    @Override
    public IAgriGenome getGenome() {
        return this.wrapped.getGenome();
    }

    private static abstract class WrappedCrop implements IAgriCrop {
        public static WrappedCrop get(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
            if(state.getBlock() instanceof BlockCropSticks) {
                return Valid(state, world, pos, stateChangeListener);
            } else {
                return Invalid(state, world, pos, stateChangeListener);
            }
        }

        private static WrappedCrop Valid(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
            return new Valid(state, world, pos, stateChangeListener);
        }

        private static WrappedCrop Invalid(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
            return new Invalid(state, world, pos, stateChangeListener);
        }

        private final World world;
        private final BlockPos pos;

        // Cached block state
        private BlockState state;

        // Listener for BlockState changes
        private final Consumer<BlockState> listener;

        private WrappedCrop(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
            this.world = Objects.requireNonNull(world);
            this.pos = Objects.requireNonNull(pos);
            this.state = Objects.requireNonNull(state);
            this.listener = stateChangeListener;

        }

        @Override
        public final World getWorld() {
            return this.world;
        }

        @Override
        public final BlockPos getPosition() {
            return this.pos;
        }

        public final BlockState getState() {
            return this.state;
        }

        public final void setBlockState(BlockState state) {
            this.state = state;
            this.listener.accept(this.getState());
        }

        private static class Valid extends WrappedCrop {
            // Pointer to Crop Sticks block implementation
            private final BlockCropSticks block;

            // Cached seed
            private AgriSeed seed;

            private Valid(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
                super(state, world, pos, stateChangeListener);
                this.block = AgriCraft.instance.getModBlockRegistry().CROP_STICKS;
                this.seed = new AgriSeed(this.getPlant(), this.getStats());
            }

            @Override
            public final boolean isValid() {
                return true;
            }

            @Override
            public IAgriGrowthStage getGrowthStage() {
                return this.block.getGrowthStage(this.getState());
            }

            @Override
            public boolean setGrowthStage(IAgriGrowthStage stage) {
                return this.getPlant().map(plant -> {
                    if(plant.getGrowthStages().contains(stage)) {
                        if(!stage.equals(this.getGrowthStage())) {
                            this.setBlockState(this.block.setGrowthStage(this.getState(), stage));
                            this.getWorld().setBlockState(this.getPosition(), this.getState());
                        }
                        return true;
                    } else {
                        return false;
                    }
                }).orElse(false);
            }

            @Override
            public boolean isCrossCrop() {
                return this.block.isCrossCrop(this.getState());
            }

            @Override
            public boolean setCrossCrop(boolean status) {
                if(this.hasPlant()) {
                    return false;
                }
                if(this.isCrossCrop() == status) {
                    return false;
                }
                this.setBlockState(this.block.setCrossCrop(this.getState(), status));
                this.getWorld().setBlockState(this.getPosition(), this.getState());
                return true;
            }

            @Override
            public boolean isFertile() {
                return this.getPlant()
                        .map(plant -> plant.getGrowConditions(this.getGrowthStage()).isMet(this.getWorld(), this.getPosition()))
                        .orElse(false);
            }

            @Override
            public boolean isMature() {
                return this.getGrowthStage().isMature();
            }

            @Nonnull
            @Override
            public Optional<IAgriSoil> getSoil() {
                return AgriApi.getSoilRegistry().get(this.getWorld().getBlockState(this.getPosition().down()));
            }

            @Override
            public void breakCrop() {
                Block.spawnDrops(this.getState(), this.getWorld(), this.getPosition());
                this.getWorld().setBlockState(this.getPosition(), Blocks.AIR.getDefaultState());
            }

            @Override
            public void applyGrowthTick() {
                if(this.getWorld().isRemote) {
                    return;
                }
                if(this.isCrossCrop()) {
                    this.crossOver();
                } else if(!this.hasPlant()) {
                    this.spawnWeeds();
                } else if(this.isMature()) {
                    this.spread();
                } else {
                    this.getPlant().ifPresent(plant -> {
                        IAgriGrowthStage stage = this.getGrowthStage();
                        double rate = plant.getGrowthChanceBase(stage) + this.getStats().getGrowth() * plant.getGrowthChanceBonus(stage) * AgriCraftConfig.growthMultiplier;
                        if(rate > this.getWorld().getRandom().nextDouble()) {
                            this.grow();
                        }
                    });
                }
            }

            public void crossOver() {
                AgriApi.getMutationEngine().attemptCross(this, this.getWorld().getRandom());
            }

            public void spawnWeeds() {

            }

            @Override
            public boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer) {
                if(this.hasPlant()) {
                    return this.getPlant().map(plant -> plant.isFertilizable(this.getGrowthStage(), fertilizer)).orElse(false);
                } else {
                    if(this.isCrossCrop()) {
                        return fertilizer.canTriggerMutation();
                    } else {
                        //TODO: weeds
                        return false;
                    }
                }
            }

            @Nonnull
            @Override
            public ActionResultType onApplyFertilizer(@Nullable IAgriFertilizer fertilizer, @Nonnull Random rand) {
                //TODO
                return ActionResultType.PASS;
            }

            @Override
            public boolean canBeHarvested() {
                return this.getPlant().map(plant -> plant.allowsHarvest(this.getGrowthStage())).orElse(false);
            }

            @Nonnull
            @Override
            public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
                if(this.canBeHarvested()) {
                    this.getPlant().ifPresent(plant -> {
                        plant.getHarvestProducts(consumer, this.getGrowthStage(), this.getStats(), this.getWorld().getRandom());
                        this.setGrowthStage(plant.getGrowthStageAfterHarvest());
                    });
                    return ActionResultType.CONSUME;
                }
                return ActionResultType.FAIL;
            }

            @Override
            public boolean canBeRaked() {
                return this.block.getWeed(this.getState()).isWeed();
            }

            @Override
            public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
                if(this.canBeRaked()) {
                    this.block.getWeed(this.getState()).onRake(consumer, player);
                    //TODO: Clear weed  and weed growth stage
                    return true;
                }
                return false;
            }

            @Override
            public boolean acceptsPlant(@Nullable IAgriPlant plant) {
                return (!this.hasPlant()) && (!this.isCrossCrop());
            }

            @Override
            public boolean setPlant(@Nullable IAgriPlant plant) {
                if(this.acceptsPlant(plant)) {
                    BlockState newState = this.block.setPlant(this.getState(), plant);
                    newState = this.block.setGrowthStage(newState, plant.getInitialGrowthStage());
                    this.setBlockState(newState);
                }
                return false;
            }

            @Nonnull
            @Override
            public Optional<IAgriPlant> removePlant() {
                if(this.hasPlant()) {
                    Optional<IAgriPlant> plant = this.getPlant();
                    // TODO: Clear plant and growth stage
                    return plant;
                }
                return Optional.empty();
            }

            @Override
            public boolean hasPlant() {
                return this.block.getPlant(this.getState()).isPlant();
            }

            @Nonnull
            @Override
            public Optional<IAgriPlant> getPlant() {
                return this.hasPlant() ? Optional.of(this.block.getPlant(this.getState())) : Optional.empty();
            }

            @Override
            public boolean acceptsSeed(@Nullable AgriSeed seed) {
                return (seed == null) || ( (!this.hasPlant()) && (!this.isCrossCrop()) );
            }

            @Override
            public boolean setSeed(@Nullable AgriSeed seed) {
                if(seed == null) {
                    return this.removePlant().isPresent();
                }
                return this.setPlant(seed.getPlant());
            }

            @Override
            public boolean hasSeed() {
                return false;
            }

            @Override
            public Optional<AgriSeed> getSeed() {
                return Optional.empty();
            }
        }

        private static class Invalid extends WrappedCrop {
            private Invalid(BlockState state, World world, BlockPos pos, Consumer<BlockState> stateChangeListener) {
                super(state, world, pos, stateChangeListener);
            }

            @Override
            public final boolean isValid() {
                return false;
            }

            @Override
            public IAgriGrowthStage getGrowthStage() {
                // TODO: default growth stage
                return null;
            }

            @Override
            public boolean setGrowthStage(IAgriGrowthStage stage) {
                return false;
            }

            @Override
            public boolean isCrossCrop() {
                return false;
            }

            @Override
            public boolean setCrossCrop(boolean status) {
                return false;
            }

            @Override
            public boolean isFertile() {
                return false;
            }

            @Override
            public boolean isMature() {
                return false;
            }

            @Nonnull
            @Override
            public Optional<IAgriSoil> getSoil() {
                return Optional.empty();
            }

            @Override
            public void breakCrop() {}

            @Override
            public void applyGrowthTick() {}

            @Override
            public boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer) {
                return false;
            }

            @Nonnull
            @Override
            public ActionResultType onApplyFertilizer(@Nullable IAgriFertilizer fertilizer, @Nonnull Random rand) {
                return ActionResultType.FAIL;
            }

            @Override
            public boolean canBeHarvested() {
                return false;
            }

            @Nonnull
            @Override
            public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
                return ActionResultType.FAIL;
            }

            @Override
            public boolean canBeRaked() {
                return false;
            }

            @Override
            public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player) {
                return false;
            }

            @Override
            public boolean acceptsPlant(@Nullable IAgriPlant plant) {
                return false;
            }

            @Override
            public boolean setPlant(@Nullable IAgriPlant plant) {
                return false;
            }

            @Nonnull
            @Override
            public Optional<IAgriPlant> removePlant() {
                return Optional.empty();
            }

            @Override
            public boolean hasPlant() {
                return false;
            }

            @Nonnull
            @Override
            public Optional<IAgriPlant> getPlant() {
                return Optional.empty();
            }

            @Override
            public boolean acceptsSeed(@Nullable AgriSeed seed) {
                return false;
            }

            @Override
            public boolean setSeed(@Nullable AgriSeed seed) {
                return false;
            }

            @Override
            public boolean hasSeed() {
                return false;
            }

            @Override
            public Optional<AgriSeed> getSeed() {
                return Optional.empty();
            }
        }
    }
}
