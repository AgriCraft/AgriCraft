package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
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

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class TileEntityCropSticks extends TileEntityBase implements IAgriCrop, IDebuggable, IAgriDisplayable {
    private static final IAgriGrowthStage NO_GROWTH = NoGrowth.getInstance();
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();
    private static final IAgriWeed NO_WEED = NoWeed.getInstance();
    private static final IAgriStatsMap NO_STATS = NoStats.getInstance();

    private IAgriPlant plant;
    private IAgriGrowthStage growth;
    private GrowthRequirement requirement;  // TODO: Implement growth requirements and cache their states

    private IAgriWeed weed;
    private IAgriGrowthStage weedGrowth;

    private IAgriGenome genome;

    private boolean crossCrop;

    public TileEntityCropSticks() {
        super(AgriCraft.instance.getModTileRegistry().crop_sticks);
        this.plant = NO_PLANT;
        this.growth = NO_GROWTH;
        this.weed = NO_WEED;
        this.weedGrowth = NO_GROWTH;
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
        return this.growth;
    }

    @Override
    public boolean setGrowthStage(@Nonnull IAgriGrowthStage stage) {
        if(this.growth.equals(stage)) {
            return false;
        }
        if(!this.plant.getGrowthStages().contains(stage)) {
            return false;
        }
        this.growth = stage;
        this.plant.onGrowth(this);
        return true;
    }

    @Override
    public boolean isCrossCrop() {
        return this.crossCrop;
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
        this.crossCrop = status;
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
        Block.spawnDrops(this.getState(), this.getWorld(), this.getPosition());
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
            this.streamNeighbours() //TODO: make this more efficient by caching neighbours
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
                    .handleMutationTick(this, this.streamNeighbours(), this.getWorld().getRandom())
                    .ifPresent(plant -> plant.onSpawned(this));
        }
    }

    protected void executePlantGrowthTick() {
        if (!this.isMature()) {
            int growth = this.getStats().getValue(AgriStatRegistry.getInstance().growthStat());
            double rate = plant.getGrowthChanceBase(this.growth) + growth * plant.getGrowthChanceBonus(this.growth) * AgriCraft.instance.getConfig().growthMultiplier();
            if (rate > this.getRandom().nextDouble()) {
                this.setGrowthStage(this.growth.getNextStage(this, this.getRandom()));
            }
        }
    }

    @Override
    @Nonnull
    public Optional<IAgriGenome> getGenome() {
        return Optional.ofNullable(this.genome);
    }

    @Override
    public void setGenome(@Nonnull IAgriGenome genome) {
        if(this.hasPlant() && !genome.equals(this.getGenome())) {
            this.genome = genome;
        }
    }

    @Nonnull
    @Override
    public IAgriStatsMap getStats() {
        return this.genome == null ? NO_STATS : this.genome.getStats();
    }

    @Override
    public boolean acceptsFertilizer(@Nonnull IAgriFertilizer fertilizer) {
        Objects.requireNonNull(fertilizer);
        if(this.isCrossCrop()) {
            return AgriCraft.instance.getConfig().allowFertilizerMutations() && fertilizer.canTriggerMutation();
        } else if(this.hasPlant()) {
            return this.plant.isFertilizable(this.getGrowthStage(), fertilizer);
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
        return plant.allowsHarvest(this.getGrowthStage(), entity);
    }

    @Nonnull
    @Override
    public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, LivingEntity entity) {
        if (this.getWorld() != null && this.canBeHarvested(entity)) {
            this.plant.getHarvestProducts(consumer, this.getGrowthStage(), this.getStats(), this.getWorld().getRandom());
            this.setGrowthStage(plant.getGrowthStageAfterHarvest());
            plant.onHarvest(this, entity);
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
        this.plant = plant;
        this.growth = plant.getInitialGrowthStage();
        return true;
    }

    @Nonnull
    @Override
    public IAgriPlant removePlant() {
        IAgriPlant plant = this.plant;
        this.plant = NO_PLANT;
        this.growth = NO_GROWTH;
        plant.onRemoved(this);
        this.genome = null;
        return plant;
    }

    @Override
    public boolean hasPlant() {
        return this.plant.isPlant();
    }

    @Nonnull
    @Override
    public IAgriPlant getPlant() {
        return this.plant;
    }

    @Override
    public boolean hasWeeds() {
        return this.weed.isWeed();
    }

    @Override
    public IAgriWeed getWeeds() {
        return this.weed;
    }

    @Override
    public IAgriGrowthStage getWeedGrowthStage() {
        return this.weedGrowth;
    }

    @Override
    public boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
        if(this.weed.equals(weed)) {
            if(this.weedGrowth.equals(stage)) {
                return false;
            }
            if(this.weed.getGrowthStages().contains(stage)) {
                this.weedGrowth = stage;
                this.weed.onGrowthTick(this);
                return true;
            }
            return false;
        } else if(weed.getGrowthStages().contains(stage)) {
            this.weed = weed;
            this.weedGrowth = stage;
            this.weed.onSpawned(this);
        }
        return false;
    }

    @Override
    public boolean removeWeed() {
        if(this.hasWeeds()) {
            this.weed = NO_WEED;
            this.weedGrowth = NO_GROWTH;
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
            this.genome = seed.getGenome().orElseThrow(() -> new IllegalArgumentException("Can not set a plant from a seed with no genome"));
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
        return Optional.ofNullable(this.hasPlant() ? new AgriSeed(this.getPlant(), this.genome) : null);
    }

    @Override
    protected void writeTileNBT(CompoundNBT tag) {
        tag.putString(AgriNBT.PLANT, this.getPlant().getId());
        tag.putString(AgriNBT.GROWTH, this.getGrowthStage().getId());
        tag.putString(AgriNBT.WEED, this.getWeeds().getId());
        tag.putString(AgriNBT.WEED_GROWTH, this.getWeedGrowthStage().getId());
        tag.putBoolean(AgriNBT.CROSS_CROP, this.isCrossCrop());
        this.getGenome().ifPresent(genome -> {
            CompoundNBT geneTag = new CompoundNBT();
            genome.writeToNBT(geneTag);
            tag.put(AgriNBT.GENOME, geneTag);
        });
    }

    @Override
    protected void readTileNBT(BlockState state, CompoundNBT tag) {
        this.plant = AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NO_PLANT);
        this.growth = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NO_GROWTH);
        this.weed = AgriApi.getWeedRegistry().get(tag.getString(AgriNBT.WEED)).orElse(NO_WEED);
        this.weedGrowth = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NO_GROWTH);
        this.crossCrop = tag.getBoolean(AgriNBT.CROSS_CROP);
        if(tag.contains(AgriNBT.GENOME)) {
            if(this.genome == null) {
                this.genome = AgriApi.getAgriGenomeBuilder(this.plant).build();
            }
            this.genome.readFromNBT(tag.getCompound(AgriNBT.GENOME));
        }
    }

    @Override
    public void addServerDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept("CROP:");
        if (this.crossCrop) {
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
