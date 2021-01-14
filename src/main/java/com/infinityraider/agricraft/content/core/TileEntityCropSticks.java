package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.misc.IAgriDisplayable;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.GrowthRequirement;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class TileEntityCropSticks extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {
    private static final IAgriGrowthStage NO_GROWTH = NoGrowth.getInstance();
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();
    private static final IAgriWeed NO_WEED = NoWeed.getInstance();
    private static final IAgriStatsMap NO_STATS = NoStats.getInstance();

    // Auto synced fields
    private final AutoSyncedField<IAgriPlant> plant;
    private final AutoSyncedField<IAgriGrowthStage> growth;
    private final AutoSyncedField<IAgriWeed> weed;
    private final AutoSyncedField<IAgriGrowthStage> weedGrowth;
    private final AutoSyncedField<Optional<IAgriGenome>> genome;
    private final AutoSyncedField<Boolean> crossCrop;
    // Growth Requirements
    private GrowthRequirement requirement;  // TODO: Implement growth requirements and cache their states
    // Cache for neighbouring crops
    private final Map<Direction, Optional<IAgriCrop>> neighbours;
    private boolean needsCaching;

    public TileEntityCropSticks() {
        // Super constructor with appropriate TileEntity Type
        super(AgriCraft.instance.getModTileRegistry().crop_sticks);
        // Initialize automatically synced fields
        this.plant = this.createField(NO_PLANT,
                (plant, tag) -> tag.putString(AgriNBT.PLANT, plant.getId()),
                (tag) -> AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NO_PLANT));
        this.growth = this.createField(NO_GROWTH,
                (growth, tag) -> tag.putString(AgriNBT.GROWTH, growth.getId()),
                (tag) -> AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NO_GROWTH));
        this.weed = this.createField(NO_WEED,
                (weed, tag) -> tag.putString(AgriNBT.WEED, weed.getId()),
                (tag) -> AgriApi.getWeedRegistry().get(tag.getString(AgriNBT.WEED)).orElse(NO_WEED));
        this.weedGrowth = this.createField(NO_GROWTH,
                (growth, tag) -> tag.putString(AgriNBT.WEED_GROWTH, growth.getId()),
                (tag) -> AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NO_GROWTH));
        this.genome = this.createField(Optional.empty(),
                (optional, tag) -> optional.ifPresent(genome -> {
                    CompoundNBT geneTag = new CompoundNBT();
                    genome.writeToNBT(geneTag);
                    tag.put(AgriNBT.GENOME, geneTag);
                }),
                (tag) -> {
                    if(tag.contains(AgriNBT.GENOME)) {
                        IAgriGenome genome = AgriApi.getAgriGenomeBuilder(this.getPlant()).build();
                        genome.readFromNBT(tag.getCompound(AgriNBT.GENOME));
                        return Optional.of(genome);
                    } else {
                        return Optional.empty();
                    }
                });
        this.crossCrop = this.createField(false,
                (bool, tag) -> tag.putBoolean(AgriNBT.CROSS_CROP, bool),
                (tag) -> tag.getBoolean(AgriNBT.CROSS_CROP));
        // Initialize neighbour cache
        this.neighbours = Maps.newEnumMap(Direction.class);
        Direction.Plane.HORIZONTAL.getDirectionValues().forEach(dir -> neighbours.put(dir, Optional.empty()));
        this.needsCaching = true;
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
                .map(Optional::get);
    }

    // Initialize neighbours cache
    protected void readNeighbours() {
        if(this.getWorld() != null) {
            Direction.Plane.HORIZONTAL.getDirectionValues().forEach(dir -> neighbours.put(dir, AgriApi.getCrop(this.getWorld(), pos)));
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
    public boolean isValid() {
        return this.getWorld() != null && !this.isRemoved();
    }

    @Override
    @Nonnull
    public BlockPos getPosition() {
        return this.getPos();
    }

    @Override
    @Nonnull
    public IAgriGrowthStage getGrowthStage() {
        return this.growth.get();
    }

    @Override
    public boolean setGrowthStage(@Nonnull IAgriGrowthStage stage) {
        if(this.growth.get().equals(stage)) {
            return false;
        }
        if(!this.getPlant().getGrowthStages().contains(stage)) {
            return false;
        }
        this.growth.set(stage);
        this.getPlant().onGrowth(this);
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
        if(this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.CROSS_CROP.apply(this.getBlockState(), status));
        }
        return true;
    }

    @Override
    public boolean isFertile() {
        return this.getWorld() != null
                && this.getPlant().getGrowConditions(this.getGrowthStage()).stream().allMatch(c -> c.isMet(this.getWorld(), this.getPosition()));
    }

    @Override
    public boolean isMature() {
        return this.getGrowthStage().isMature();
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil() {
        return Optional.ofNullable(this.getWorld())
                .map(world -> AgriApi.getSoilRegistry().get(world.getBlockState(this.getPosition().down())))
                .flatMap(soils -> soils.stream().findAny());
    }

    @Override
    public void breakCrop(@Nullable LivingEntity entity) {
        if(this.getWorld() == null) {
            return;
        }
        IAgriPlant plant = this.getPlant();
        IAgriWeed weed = this.getWeeds();
        Block.spawnDrops(this.getBlockState(), this.getWorld(), this.getPosition());
        this.getWorld().setBlockState(this.getPosition(), Blocks.AIR.getDefaultState());
        plant.onBroken(this, entity);
        weed.onBroken(this, entity);
    }

    @Override
    public void applyGrowthTick() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        // Decide if the weeds receives the growth tick or not
        if (this.rollForWeedAction()) {
            // Weeds have the word
            this.executeWeedGrowthTick();
        } else if(this.isCrossCrop()) {
            // mutation tick
            this.executeCrossGrowthTick();
        } else if(this.isFertile()) {
            // plant growth tick
            this.executePlantGrowthTick();
        }
    }

    protected boolean rollForWeedAction() {
        if(AgriCraft.instance.getConfig().disableWeeds()) {
            return false;
        }
        if(this.hasPlant()) {
            int resist = this.getStats().getValue(AgriStatRegistry.getInstance().resistanceStat());
            int max = AgriStatRegistry.getInstance().resistanceStat().getMax();
            // At 1 resist, 50/50 chance for weed growth tick
            // At 10 resist, 0% chance
            return this.getRandom().nextInt(max) >= (max + resist)/2;
        }
        return this.getRandom().nextBoolean();
    }

    protected void executeWeedGrowthTick() {
        if (!this.hasWeeds()) {
            //The aren't weeds yet, try to spawn new weeds
            this.spawnWeeds();
        } else {
            // There are weeds already, apply the growth tick
            if (this.getWeedGrowthStage().isMature()) {
                //Weeds are mature, try killing the plant
                this.tryWeedKillPlant();
                //Weeds are mature, try spreading
                this.spreadWeeds();
            } else {
                // Weeds are not mature yet, increment their growth
                this.setWeed(this.getWeeds(), this.getWeedGrowthStage().getNextStage(this, this.getRandom()));
            }
        }
    }

    protected void spawnWeeds() {
        AgriApi.getWeedRegistry().stream()
                .filter(IAgriWeed::isWeed)
                .filter(weed -> this.getRandom().nextDouble() < weed.spawnChance(this))
                .findAny()
                .ifPresent(weed -> this.setWeed(weed, weed.getInitialGrowthStage()));
    }

    protected void spreadWeeds() {
        if(AgriCraft.instance.getConfig().weedsCanSpread()) {
            this.streamNeighbours()
                    .filter(IAgriCrop::isValid)
                    .filter(crop -> !crop.hasWeeds())
                    .filter(crop -> this.rollForWeedAction())
                    .forEach(crop -> crop.setWeed(this.getWeeds(), this.getWeeds().getInitialGrowthStage()));
        }
    }

    protected void tryWeedKillPlant() {
        if(AgriCraft.instance.getConfig().matureWeedsKillPlant()) {
            if(this.hasPlant() && this.rollForWeedAction()) {
                IAgriPlant plant = this.getPlant();
                this.setPlant(NO_PLANT);
                plant.onRemoved(this);
            }
        }
    }

    protected void executeCrossGrowthTick() {
        // Do not do mutation growth ticks if the plant has weeds
        if(!this.hasWeeds()) {
            AgriApi.getAgriMutationHandler().getActiveMutationEngine()
                    .handleMutationTick(this, this.streamNeighbours(), this.getRandom())
                    .ifPresent(plant -> plant.onSpawned(this));
        }
    }

    protected void executePlantGrowthTick() {
        if (!this.isMature()) {
            int growth = this.getStats().getValue(AgriStatRegistry.getInstance().growthStat());
            double rate = this.getPlant().getGrowthChanceBase(this.getGrowthStage())
                    + growth * this.getPlant().getGrowthChanceBonus(this.getGrowthStage()) * AgriCraft.instance.getConfig().growthMultiplier();
            if (rate > this.getRandom().nextDouble()) {
                this.setGrowthStage(this.getGrowthStage().getNextStage(this, this.getRandom()));
            }
        }
    }

    @Override
    @Nonnull
    public Optional<IAgriGenome> getGenome() {
        return this.genome.get();
    }

    @Override
    public void setGenome(@Nonnull IAgriGenome genome) {
        Optional<IAgriGenome> opt = Optional.of(genome);
        if(this.hasPlant() && !opt.equals(this.getGenome())) {
            this.genome.set(opt);
        }
    }

    @Nonnull
    @Override
    public IAgriStatsMap getStats() {
        return this.getGenome().map(IAgriStatProvider::getStats).orElse(NO_STATS);
    }

    @Override
    public boolean acceptsFertilizer(@Nonnull IAgriFertilizer fertilizer) {
        Objects.requireNonNull(fertilizer);
        if(this.isCrossCrop()) {
            return AgriCraft.instance.getConfig().allowFertilizerMutations() && fertilizer.canTriggerMutation();
        } else if(this.hasPlant()) {
            return this.getPlant().isFertilizable(this.getGrowthStage(), fertilizer);
        } else {
            return fertilizer.canTriggerWeeds();
        }
    }

    @Override
    @Nonnull
    public ActionResultType onApplyFertilizer(@Nonnull IAgriFertilizer fertilizer, @Nonnull Random rand) {
        // Validate
        Preconditions.checkNotNull(rand);

        // Do stuff
        if (this.isRemote()) {
            // The client doesn't need to do anything special.
            return ActionResultType.PASS;
        } else if (!this.acceptsFertilizer(fertilizer)) {
            // This method has been called in error. Make a note and block the tick.
            AgriCore.getCoreLogger().warn("onApplyFertilizer should not be called if acceptFertilizer is false!");
            return ActionResultType.FAIL;
        } else {
            // The regular growth tick method handles the rest, now that the special cases have been filtered out
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public boolean canBeHarvested(@Nullable LivingEntity entity) {
        return this.getPlant().allowsHarvest(this.getGrowthStage(), entity);
    }

    @Nonnull
    @Override
    public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, LivingEntity entity) {
        if (this.getWorld() != null && this.canBeHarvested(entity)) {
            this.getPlant().getHarvestProducts(consumer, this.getGrowthStage(), this.getStats(), this.getWorld().getRandom());
            this.setGrowthStage(this.getPlant().getGrowthStageAfterHarvest());
            this.getPlant().onHarvest(this, entity);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity) {
        return this.getWeeds().isWeed();
    }

    @Override
    public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
        if(this.getWorld() == null || this.getWorld().isRemote()) {
            return false;
        }
        IAgriWeed weed = this.getWeeds();
        if (weed.isWeed()) {
            this.setWeed(NO_WEED, NO_GROWTH);
            weed.onRake(consumer, entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsPlant(@Nonnull IAgriPlant plant) {
        return (!this.hasPlant()) && (!this.isCrossCrop());
    }

    @Override
    public boolean setPlant(@Nonnull IAgriPlant plant, @Nullable LivingEntity entity) {
        boolean result = this.setPlantImpl(plant);
        if(result) {
            plant.onPlanted(this, entity);
        }
        return result;
    }

    @Override
    public boolean setPlant(@Nonnull IAgriPlant plant) {
        boolean result = this.setPlantImpl(plant);
        if(result) {
            plant.onSpawned(this);
        }
        return result;
    }

    private boolean setPlantImpl(@Nonnull IAgriPlant plant) {
        if(this.hasPlant()) {
            return false;
        }
        if(this.isCrossCrop()) {
            return false;
        }
        this.plant.set(plant);
        this.growth.set(plant.getInitialGrowthStage());
        if(this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(this.getBlockState(), true));
        }
        return true;
    }

    @Nonnull
    @Override
    public IAgriPlant removePlant() {
        IAgriPlant plant = this.plant.get();
        this.plant.set(NO_PLANT);
        this.growth.set(NO_GROWTH);
        plant.onRemoved(this);
        this.genome.set(Optional.empty());
        if(this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(this.getBlockState(), false));
        }
        return plant;
    }

    @Override
    public boolean hasPlant() {
        return this.plant.get().isPlant();
    }

    @Nonnull
    @Override
    public IAgriPlant getPlant() {
        return this.plant.get();
    }

    @Override
    public boolean hasWeeds() {
        return this.weed.get().isWeed();
    }

    @Nonnull
    @Override
    public IAgriWeed getWeeds() {
        return this.weed.get();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getWeedGrowthStage() {
        return this.weedGrowth.get();
    }

    @Override
    public boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
        if(this.weed.get().equals(weed)) {
            if(this.weedGrowth.get().equals(stage)) {
                return false;
            }
            if(this.weed.get().getGrowthStages().contains(stage)) {
                this.weedGrowth.set(stage);
                this.weed.get().onGrowthTick(this);
                return true;
            }
            return false;
        } else if(weed.getGrowthStages().contains(stage)) {
            this.weed.set(weed);
            this.weedGrowth.set(stage);
            this.weed.get().onSpawned(this);
            if(this.getWorld() != null) {
                this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(this.getBlockState(), true));
            }
        }
        return false;
    }

    @Override
    public boolean removeWeed() {
        if(this.hasWeeds()) {
            this.weed.set(NO_WEED);
            this.weedGrowth.set(NO_GROWTH);
            if(this.getWorld() != null) {
                this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(this.getBlockState(), false));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsSeed(@Nonnull AgriSeed seed) {
        return this.acceptsPlant(seed.getPlant());
    }

    @Override
    public boolean setSeed(@Nonnull AgriSeed seed) {
        if(this.setPlant(seed.getPlant())) {
            this.setGenome(seed.getGenome().orElseThrow(() -> new IllegalArgumentException("Can not set a plant from a seed with no genome")));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSeed() {
        return this.removePlant().isPlant();
    }

    @Override
    public boolean hasSeed() {
        return this.hasPlant();
    }

    @Override
    public Optional<AgriSeed> getSeed() {
        return this.getGenome().flatMap(genome -> Optional.ofNullable(this.hasPlant() ? new AgriSeed(this.getPlant(), genome) : null));
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {
        // No need to write anything since everything is covered by the AutoSyncedFields
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        // No need to read anything since everything is covered by the AutoSyncedFields
        // A cache update will be required though (either on the client, or on the server after being loaded)
        this.needsCaching = true;
    }

    @Override
    public void addServerDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept("CROP:");
        if (this.isCrossCrop()) {
            consumer.accept(" - This is a crosscrop");
        } else {
            final IAgriPlant plant = this.getPlant();
            final IAgriWeed weed = this.getWeeds();
            final IAgriStatsMap stats = this.getStats();
            if(plant.isPlant()) {
                consumer.accept(" - This crop has a plant");
            }
            if(weed.isWeed()) {
                consumer.accept(" - This crop has weeds");
            }
            consumer.accept(" - Plant Id: " + plant.getId());
            consumer.accept(" - Plant Stage: " + this.getGrowthStage());
            consumer.accept(" - Plant Stages: " + plant.getGrowthStages());
            consumer.accept(" - Weed Id: " + weed.getId());
            consumer.accept(" - Weed Stage: " + this.getWeedGrowthStage());
            consumer.accept(" - Weed Stages: " + weed.getGrowthStages());
            consumer.accept(" - stats: " + stats);
            consumer.accept(" - Fertile: " + this.isFertile());
            consumer.accept(" - Mature: " + this.isMature());
            consumer.accept(" - AgriSoil: " + this.getSoil());
        }
    }

    @Override
    public void addClientDebugInfo(@Nonnull Consumer<String> consumer) {

    }

    @Override
    public void addDisplayInfo(@Nonnull Consumer<ITextComponent> consumer) {
        // Validate
        Preconditions.checkNotNull(consumer);

        // Add plant information
        if (this.hasPlant()) {
            //Add the plant data.
            consumer.accept(AgriToolTips.getPlantTooltip(this.getPlant()));
            consumer.accept(AgriToolTips.getGrowthTooltip(this.getGrowthStage()));
            //Add the stats
            this.getStats().addTooltips(consumer);
            //Add the fertility information.
            if(this.isFertile()) {
                consumer.accept(AgriToolTips.FERTILE);
            } else {
                consumer.accept(AgriToolTips.NOT_FERTILE);
            }
        } else {
            consumer.accept(AgriToolTips.NO_PLANT);
        }

        // Add weed information
        if(this.hasWeeds()) {
            consumer.accept(AgriToolTips.getWeedTooltip(this.getWeeds()));
            consumer.accept(AgriToolTips.getWeedGrowthTooltip(this.getWeedGrowthStage()));
        } else {
            consumer.accept(AgriToolTips.NO_WEED);
        }

        // Add Soil Information
        this.getSoil().map(soil -> {
            consumer.accept(AgriToolTips.getSoilTooltip(soil));
            return null;
        }).orElseGet(() -> {
            consumer.accept(AgriToolTips.getUnknownTooltip(AgriToolTips.SOIL));
            return null;
        });
    }
}
