package com.infinityraider.agricraft.content.core;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.requirement.RequirementCache;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public abstract class TileEntityCropBase extends TileEntityBase implements IAgriCrop, IDebuggable {
    // Model properties
    public static final ModelProperty<IAgriPlant> PROPERTY_PLANT = new ModelProperty<>();
    public static final ModelProperty<IAgriGrowthStage> PROPERTY_PLANT_GROWTH = new ModelProperty<>();
    public static final ModelProperty<IAgriWeed> PROPERTY_WEED = new ModelProperty<>();
    public static final ModelProperty<IAgriGrowthStage> PROPERTY_WEED_GROWTH = new ModelProperty<>();

    // Default instances
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();
    private static final IAgriGrowthStage NO_GROWTH = NoGrowth.getInstance();
    private static final IAgriWeed NO_WEED = NoWeed.getInstance();
    private static final IAgriStatsMap NO_STATS = NoStats.getInstance();

    // Model data
    private final ModelDataMap data;
    // Auto synced fields
    private final AutoSyncedField<Optional<IAgriGenome>> genome;
    private final AutoSyncedField<IAgriGrowthStage> growth;
    private final AutoSyncedField<IAgriWeed> weed;
    private final AutoSyncedField<IAgriGrowthStage> weedGrowth;
    // Growth Requirements
    private RequirementCache requirement;

    public TileEntityCropBase(TileEntityType<?> type) {
        // Super constructor with appropriate TileEntity Type
        super(type);

        // Initialize model data map
        this.data = new ModelDataMap.Builder()
                .withInitial(PROPERTY_PLANT, NO_PLANT)
                .withInitial(PROPERTY_PLANT_GROWTH, NO_GROWTH)
                .withInitial(PROPERTY_WEED, NO_WEED)
                .withInitial(PROPERTY_WEED_GROWTH, NO_GROWTH)
                .build();

        // Initialize automatically synced fields
        this.genome = this.createAutoSyncedField(
                Optional.empty(),
                (optional, tag) -> optional.ifPresent(genome -> {
                    CompoundNBT geneTag = new CompoundNBT();
                    genome.writeToNBT(geneTag);
                    tag.put(AgriNBT.GENOME, geneTag);
                }),
                (tag) -> {
                    if (tag.contains(AgriNBT.GENOME)) {
                        IAgriGenome genome = AgriApi.getAgriGenomeBuilder(NO_PLANT).build();
                        genome.readFromNBT(tag.getCompound(AgriNBT.GENOME));
                        return Optional.of(genome);
                    } else {
                        return Optional.empty();
                    }
                },
                CoreHandler::isInitialized,
                Optional.empty());

        this.growth = this.getAutoSyncedFieldBuilder(NO_GROWTH,
                (growth, tag) -> tag.putString(AgriNBT.GROWTH, growth.getId()),
                (tag) -> AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NO_GROWTH),
                CoreHandler::isInitialized,
                NO_GROWTH).withCallBack((stage) -> {
                    this.getModelData().setData(PROPERTY_PLANT_GROWTH, stage);
                    this.requirement = RequirementCache.create(this);
                }).withRenderUpdate().build();

        this.weed = this.getAutoSyncedFieldBuilder(NO_WEED,
                (weed, tag) -> tag.putString(AgriNBT.WEED, weed.getId()),
                (tag) -> AgriApi.getWeedRegistry().get(tag.getString(AgriNBT.WEED)).orElse(NO_WEED),
                CoreHandler::isInitialized,
                NO_WEED).withCallBack(weed -> this.getModelData().setData(PROPERTY_WEED, weed)).withRenderUpdate().build();

        this.weedGrowth = this.getAutoSyncedFieldBuilder(NO_GROWTH,
                (growth, tag) -> tag.putString(AgriNBT.WEED_GROWTH, growth.getId()),
                (tag) -> AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NO_GROWTH),
                CoreHandler::isInitialized,
                NO_GROWTH).withCallBack(stage -> this.getModelData().setData(PROPERTY_WEED_GROWTH, stage)).withRenderUpdate().build();

        // Initialize growth requirement cache
        this.requirement = RequirementCache.create(this);
    }

    @Override
    public void dropItem(ItemStack item) {
        if(this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        this.getWorld().addEntity(new ItemEntity(
                this.getWorld(),
                this.getPos().getX(),
                this.getPos().getY(),
                this.getPos().getZ(),
                item));
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
        if(!this.checkGrowthSpace(this.getPlant(), stage)) {
            return false;
        }
        this.growth.set(stage);
        this.handlePlantUpdate(false);
        return true;
    }

    @SuppressWarnings("deprecation")
    private boolean checkGrowthSpace(IAgriGrowable plant, IAgriGrowthStage stage) {
        if(this.getWorld() == null) {
            return false;
        }
        double height = plant.getPlantHeight(stage);
        while(height > 16) {
            int offset = ((int) height) / 16;
            BlockPos pos = this.getPos().up(offset);
            BlockState state = this.getWorld().getBlockState(pos);
            if(!state.isAir(this.getWorld(), pos)) {
                return false;
            }
            height -= 16;
        }
        return true;
    }

    @Override
    public boolean isFertile() {
        return this.getWorld() != null
                && this.checkGrowthSpace(this.getPlant(), this.getGrowthStage())
                && this.requirement.isMet();
    }

    @Override
    public boolean isMature() {
        return this.getGrowthStage().isMature();
    }

    @Override
    public boolean isFullyGrown() {
        return this.getGrowthStage().isFinal();
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil() {
        return Optional.ofNullable(this.getWorld())
                .flatMap(world -> AgriApi.getSoil(world, this.getPosition().down()));
    }

    @Override
    public void breakCrop(@Nullable LivingEntity entity) {
        if(this.getWorld() == null) {
            return;
        }
        if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Pre(this, entity))) {
            IAgriPlant plant = this.getPlant();
            IAgriWeed weed = this.getWeeds();
            Block.spawnDrops(this.getBlockState(), this.getWorld(), this.getPosition(), this);
            this.getWorld().setBlockState(this.getPosition(), Blocks.AIR.getDefaultState());
            plant.onBroken(this, entity);
            weed.onBroken(this, entity);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Post(this, entity));
        }
    }

    @Override
    public void applyGrowthTick() {
        if (this.getWorld() == null || this.getWorld().isRemote) {
            return;
        }
        if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.General.Pre(this))) {
            // Decide if the weeds receives the growth tick or not
            if (this.rollForWeedAction()) {
                // Weeds have the word
                this.executeWeedGrowthTick();
            } else if (this.isCrossCrop()) {
                // mutation tick
                this.executeCrossGrowthTick();
            } else if (this.isFertile()) {
                // plant growth tick
                this.executePlantGrowthTick();
            }
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.General.Post(this));
        }
    }

    protected boolean rollForWeedAction() {
        if(AgriCraft.instance.getConfig().disableWeeds()) {
            return false;
        }
        if(this.hasPlant()) {
            int resist = this.getStats().getResistance();
            int max = AgriStatRegistry.getInstance().resistanceStat().getMax();
            // At 1 resist, 50/50 chance for weed growth tick
            // At 10 resist, 0% chance
            return  this.getRandom().nextInt(max) >= (max + resist)/2;
        }
        return this.getRandom().nextBoolean();
    }

    protected void executeWeedGrowthTick() {
        if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Weeds.Pre(this))) {
            if (!this.hasWeeds()) {
                //The aren't weeds yet, try to spawn new weeds
                this.spawnWeeds();
            } else {
                // There are weeds already, apply the growth tick
                if (this.getWeedGrowthStage().isFinal()) {
                    //Weeds are mature, try killing the plant
                    this.tryWeedKillPlant();
                    //Weeds are mature, try spreading
                    this.spreadWeeds();
                } else {
                    // Weeds are not mature yet, increment their growth
                    double f = this.getSoil().map(IAgriSoil::getGrowthModifier).orElse(1.0D);
                    if(this.getRandom().nextDouble() < f*this.getWeeds().getGrowthChance(this.getWeedGrowthStage())) {
                        this.setWeed(this.getWeeds(), this.getWeedGrowthStage().getNextStage(this, this.getRandom()));
                    }
                }
            }
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Weeds.Post(this));
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
        if(AgriCraft.instance.getConfig().allowAggressiveWeeds() && this.getWeeds().isAggressive()) {
            this.streamNeighbours()
                    .filter(IAgriCrop::isValid)
                    .filter(crop -> !crop.hasWeeds())
                    .filter(crop -> this.rollForWeedAction())
                    .forEach(crop -> crop.setWeed(this.getWeeds(), this.getWeeds().getInitialGrowthStage()));
        }
    }

    protected void tryWeedKillPlant() {
        if(AgriCraft.instance.getConfig().allowLethalWeeds() && this.getWeeds().isLethal()) {
            if(this.hasPlant() && this.rollForWeedAction()) {
                IAgriGrowthStage current = this.getGrowthStage();
                IAgriGrowthStage previous = current.getPreviousStage(this, this.getRandom());
                if(current.equals(previous)) {
                    this.removeSeed();
                } else {
                    this.setGrowthStage(previous);
                }
            }
        }
    }

    protected void executeCrossGrowthTick() {
        // Do not do mutation growth ticks if the plant has weeds
        if(!this.hasWeeds() && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Cross.Pre(this))) {
            if(AgriApi.getAgriMutationHandler().getActiveMutationEngine().handleMutationTick(this, this.streamNeighbours(), this.getRandom())) {
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Cross.Post(this));
            }
        }
    }

    protected void executePlantGrowthTick() {
        if (!this.getGrowthStage().isFinal()) {
            BlockState state = this.getBlockState();
            if (this.calculateGrowthRate() > this.getRandom().nextDouble()
                    && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Plant.Pre(this))
                    && !MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Pre(this.getWorld(), this.getPos(), state))) {
                this.setGrowthStage(this.getGrowthStage().getNextStage(this, this.getRandom()));
                this.getPlant().onGrowth(this);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Plant.Post(this));
                MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(this.getWorld(), this.getPos(), state, state));
            }
        }
    }

    protected double calculateGrowthRate() {
        int growth = this.getStats().getGrowth();
        double soilFactor = this.getSoil().map(IAgriSoil::getGrowthModifier).orElse(1.0D);
        return soilFactor * (this.getPlant().getGrowthChanceBase(this.getGrowthStage())
            + growth * this.getPlant().getGrowthChanceBonus(this.getGrowthStage()) * AgriCraft.instance.getConfig().growthMultiplier());
    }

    @Override
    @Nonnull
    public Optional<IAgriGenome> getGenome() {
        return this.genome.get();
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
            return (!this.isFullyGrown()) && this.getPlant().isFertilizable(this.getGrowthStage(), fertilizer);
        } else {
            return fertilizer.canTriggerWeeds();
        }
    }

    @Override
    public void onApplyFertilizer(@Nonnull IAgriFertilizer fertilizer, @Nonnull Random rand) {

    }

    @Override
    public boolean canBeHarvested(@Nullable LivingEntity entity) {
        return this.getPlant().allowsHarvest(this.getGrowthStage(), entity);
    }

    @Nonnull
    @Override
    public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, LivingEntity entity) {
        if (this.getWorld() != null && this.canBeHarvested(entity)) {
            if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Pre(this, entity))) {
                this.getPlant().getHarvestProducts(consumer, this.getGrowthStage(), this.getStats(), this.getWorld().getRandom());
                this.setGrowthStage(this.getPlant().getGrowthStageAfterHarvest());
                this.getPlant().onHarvest(this, entity);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Post(this, entity));
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
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
            IAgriGrowthStage stage = this.getWeedGrowthStage();
            this.setWeed(NO_WEED, NO_GROWTH);
            weed.onRake(stage, consumer, this.getRandom(), entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPlant() {
        return this.getGenome().map(IAgriGenome::getPlant).map(IAgriPlant::isPlant).orElse(false);
    }

    @Nonnull
    @Override
    public IAgriPlant getPlant() {
        return this.getGenome().map(IAgriGenome::getPlant).orElse(NO_PLANT);
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
        if(this.getWeeds().equals(weed)) {
            if(this.weedGrowth.get().equals(stage)) {
                return false;
            }
            if(this.getWeeds().getGrowthStages().contains(stage) && this.checkGrowthSpace(weed, stage)) {
                this.weedGrowth.set(stage);
                this.getWeeds().onGrowthTick(this);
                this.handlePlantUpdate(false);
                return true;
            }
            return false;
        } else if(weed.getGrowthStages().contains(stage) && this.checkGrowthSpace(weed, stage)) {
            if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Weed.Pre(this, weed))) {
                this.setWeedImpl(weed, stage);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Weed.Post(this, weed));
                return true;
            }
        }
        return false;
    }

    protected void setWeedImpl(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
        this.weed.set(weed);
        this.weedGrowth.set(stage);
        this.getWeeds().onSpawned(this);
        this.handlePlantUpdate(false);
    }

    @Override
    public boolean removeWeed() {
        if(this.hasWeeds()) {
            this.weed.set(NO_WEED);
            this.weedGrowth.set(NO_GROWTH);
            this.handlePlantUpdate(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsSeed(@Nonnull AgriSeed seed) {
        return !this.hasSeed() && !this.isCrossCrop();
    }

    @Override
    public boolean setGenome(@Nonnull IAgriGenome genome) {
        if(!this.hasPlant() && !this.isCrossCrop() && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Pre(this, genome))) {
            this.setGenomeImpl(genome);
            this.getPlant().onSpawned(this);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Post(this, genome));
            return true;
        }
        return false;
    }

    @Override
    public boolean plantSeed(@Nonnull AgriSeed seed) {
        return this.plantSeed(seed, null);
    }

    @Override
    public boolean plantSeed(@Nonnull AgriSeed seed, @Nullable LivingEntity entity) {
        if (this.acceptsSeed(seed) && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Pre(this, seed, entity))) {
            this.setGenomeImpl(seed.getGenome());
            this.getPlant().onPlanted(this, entity);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Post(this, seed, entity));
            return true;
        }
        return false;
    }

    protected void setGenomeImpl(@Nonnull IAgriGenome genome) {
        this.genome.set(Optional.of(genome));
        this.growth.set(genome.getPlant().getInitialGrowthStage());
        this.handlePlantUpdate(true);
    }

    @Override
    public boolean removeSeed() {
        if(this.hasSeed()) {
            this.getPlant().onRemoved(this);
            this.genome.set(Optional.empty());
            this.handlePlantUpdate(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasSeed() {
        return this.hasPlant();
    }

    @Override
    public Optional<AgriSeed> getSeed() {
        return this.getGenome().map(AgriSeed::new);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {
        // No need to write anything since everything is covered by the AutoSyncedFields
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        // No need to read anything since everything is covered by the AutoSyncedFields
    }

    protected void handlePlantUpdate(boolean resetBrightness)  {
        if(this.getWorld() != null) {
            BlockState state = this.getBlockState();
            boolean plant = this.hasPlant() || this.hasWeeds();
            // Update block state
            if(resetBrightness && BlockCropSticks.LIGHT.fetch(state) > 0) {
                this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(BlockCropSticks.LIGHT.apply(state), plant));
            } else {
                if (BlockCropSticks.PLANT.fetch(state) != plant) {
                    this.getWorld().setBlockState(this.getPosition(), BlockCropSticks.PLANT.apply(state, plant));
                }
            }
            // Update growth requirement
            this.requirement.flush();
            this.requirement = RequirementCache.create(this);
            // Update model data
            this.getModelData().setData(PROPERTY_PLANT, this.getPlant());
            this.getModelData().setData(PROPERTY_PLANT_GROWTH, this.getGrowthStage());
            this.getModelData().setData(PROPERTY_WEED, this.getWeeds());
            this.getModelData().setData(PROPERTY_WEED_GROWTH, this.getWeedGrowthStage());
        }
    }

    public void mimicFrom(IAgriCrop other) {
        other.getGenome().ifPresent(genome -> {
            this.setGenomeImpl(genome.clone());
            this.setGrowthStage(other.getGrowthStage());
        });
        this.setWeedImpl(other.getWeeds(), other.getWeedGrowthStage());
    }

    @Override
    public void onChunkUnloaded() {
        this.requirement.flush();
        super.onChunkUnloaded();
    }

    @Override
    public void remove() {
        this.requirement.flush();
        super.remove();
    }

    @Override
    public void addServerDebugInfo(@Nonnull Consumer<String> consumer) {
        Preconditions.checkNotNull(consumer);
        consumer.accept("CROP:");
        if(this.hasCropSticks()) {
            consumer.accept(" - Crop Sticks Present");
        }
        if (this.isCrossCrop()) {
            consumer.accept(" - This is a cross crop");
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
            consumer.accept(" - Fully Grown: " + this.isFullyGrown());
            consumer.accept(" - AgriSoil: " + this.getSoil());
        }
    }

    @Override
    public void addClientDebugInfo(@Nonnull Consumer<String> consumer) {
        this.addServerDebugInfo(consumer);
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
            this.requirement.addTooltip(consumer);
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
            return true;
        }).orElseGet(() -> {
            consumer.accept(AgriToolTips.getUnknownTooltip(AgriToolTips.SOIL));
            return false;
        });
    }

    @Nonnull
    @Override
    public final ModelDataMap getModelData() {
        return this.data;
    }
}
