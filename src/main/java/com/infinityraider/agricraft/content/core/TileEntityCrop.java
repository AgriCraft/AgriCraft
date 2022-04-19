package com.infinityraider.agricraft.content.core;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.requirement.RequirementCache;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.util.CropHelper;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TileEntityCrop  extends TileEntityBase implements IAgriCrop, IDebuggable {
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

    // Capabilities
    private final LazyOptional<IAgriCrop> cropCapability;

    // Cache for neighbouring crops
    private final Map<Direction, Optional<IAgriCrop>> neighbours;
    private boolean needsCaching;

    public TileEntityCrop(BlockPos pos, BlockState state) {
        // Super constructor with appropriate TileEntity Type
        super(AgriTileRegistry.getInstance().crop.get(), pos, state);

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
                    CompoundTag geneTag = new CompoundTag();
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
        this.cropCapability = LazyOptional.of(() -> this);

        // Initialize neighbour cache
        this.neighbours = Maps.newEnumMap(Direction.class);
        Direction.Plane.HORIZONTAL.stream().forEach(dir -> neighbours.put(dir, Optional.empty()));
        this.needsCaching = true;
    }

    @Override
    public void dropItem(ItemStack item) {
        if(this.getLevel() == null || this.getLevel().isClientSide()) {
            return;
        }
        this.getLevel().addFreshEntity(new ItemEntity(
                this.getLevel(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                this.getPosition().getZ(),
                item));
    }

    @Override
    public boolean isValid() {
        return this.getLevel() != null && !this.isRemoved();
    }

    @Override
    @Nonnull
    public BlockPos getPosition() {
        return this.getBlockPos();
    }

    @Nonnull
    @Override
    public FluidState getFluidState() {
        return this.getBlockState().getFluidState();
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
        this.handlePlantUpdate();
        return true;
    }

    @Override
    public boolean hasCropSticks() {
        return BlockCrop.STATE.fetch(this.getBlockState()).hasSticks();
    }

    @Override
    public boolean isCrossCrop() {
        return BlockCrop.STATE.fetch(this.getBlockState()).isCross();
    }

    @Override
    public boolean setCrossCrop(boolean status) {
        Level world = this.getLevel();
        if(world == null) {
            return false;
        }
        if(status) {
            if (this.hasPlant()) {
                return false;
            }
            if (this.hasWeeds()) {
                return false;
            }
            if (this.isCrossCrop() == status) {
                return false;
            }
            world.setBlock(this.getPosition(), BlockCrop.STATE.apply(this.getBlockState(), BlockCrop.CropState.DOUBLE_STICKS), 3);
            BlockCrop.VARIANT.fetch(this.getBlockState()).playCropStickSound(world, this.getPosition());
            return true;
        } else {
            if(this.isCrossCrop()) {
                return false;
            } else {
                world.setBlock(this.getPosition(), BlockCrop.STATE.apply(this.getBlockState(), BlockCrop.CropState.SINGLE_STICKS), 3);
                BlockCrop.VARIANT.fetch(this.getBlockState()).playCropStickSound(world, this.getPosition());
                return true;
            }
        }
    }

    protected boolean checkGrowthSpace(IAgriGrowable plant, IAgriGrowthStage stage) {
        return CropHelper.checkGrowthSpace(this.getLevel(),this.getBlockPos(), plant, stage);
    }

    @Override
    public IAgriGrowthResponse getFertilityResponse() {
        if(this.getLevel() == null) {
            return IAgriGrowthResponse.INFERTILE;
        }
        if(!this.checkGrowthSpace(this.getPlant(), this.getGrowthStage())) {
            return IAgriGrowthResponse.INFERTILE;
        }
        return this.requirement.check();
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
        return Optional.ofNullable(this.getLevel())
                .flatMap(world -> AgriApi.getSoil(world, this.getPosition().below()));
    }

    @Override
    public void breakCrop(@Nullable LivingEntity entity) {
        if(this.getLevel() == null) {
            return;
        }
        if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Pre(this, entity))) {
            IAgriPlant plant = this.getPlant();
            IAgriWeed weed = this.getWeeds();
            Block.dropResources(this.getBlockState(), this.getLevel(), this.getPosition(), this);
            this.getLevel().setBlock(this.getPosition(), Blocks.AIR.defaultBlockState(), 3);
            plant.onBroken(this, entity);
            weed.onBroken(this, entity);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Post(this, entity));
        }
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
        if(newState.getBlock() instanceof BlockCrop) {
            if(this.getLevel() != null) {
                this.neighbours.put(direction, AgriApi.getCrop(this.getLevel(), pos));
            }
        } else {
            this.neighbours.put(direction, Optional.empty());
        }
    }

    @Override
    public void applyGrowthTick() {
        if (this.getLevel() == null || this.getLevel().isClientSide()) {
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
            } else {
                IAgriGrowthResponse fertility = this.getFertilityResponse();
                if(fertility.killInstantly()) {
                    // kill plant
                    fertility.onPlantKilled(this);
                    this.removeGenome();
                } else if(fertility.isLethal()) {
                    // reverse growth stage
                    this.revertGrowthStage();
                } else if(fertility.isFertile()) {
                    // plant growth tick
                    this.executePlantGrowthTick();
                }
            }
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.General.Post(this));
        }
    }

    // method to allow for overriding
    protected boolean rollForWeedAction() {
        return CropHelper.rollForWeedAction(this);
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

    // Method to allow for overrides
    protected void spawnWeeds() {
        CropHelper.spawnWeeds(this);
    }

    // Method to allow for overrides
    protected void spreadWeeds() {
        CropHelper.spreadWeeds(this);
    }

    protected void tryWeedKillPlant() {
        if(AgriCraft.instance.getConfig().allowLethalWeeds() && this.getWeeds().isLethal()) {
            if(this.hasPlant() && this.rollForWeedAction()) {
                this.revertGrowthStage();
            }
        }
    }

    protected void revertGrowthStage() {
        IAgriGrowthStage current = this.getGrowthStage();
        IAgriGrowthStage previous = current.getPreviousStage(this, this.getRandom());
        if(current.equals(previous)) {
            this.removeGenome();
        } else {
            this.setGrowthStage(previous);
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
            if (this.calculateGrowthRate() > this.getRandom().nextDouble() && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Plant.Pre(this))) {
                // also do the MinecraftForge BlockEvent for crop growth
                BlockEvent.CropGrowEvent.Pre blockEvent = new BlockEvent.CropGrowEvent.Pre(this.getLevel(), this.getBlockPos(), state);
                MinecraftForge.EVENT_BUS.post(blockEvent);
                if(blockEvent.getResult() == Event.Result.ALLOW || blockEvent.getResult() == Event.Result.DEFAULT) {
                    this.setGrowthStage(this.getGrowthStage().getNextStage(this, this.getRandom()));
                    this.getPlant().onGrowth(this);
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Grow.Plant.Post(this));
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(this.getLevel(), this.getBlockPos(), state, state));
                }
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
            return !this.isFullyGrown()
                    && fertilizer.canFertilize(this)
                    && this.getPlant().isFertilizable(this.getGrowthStage(), fertilizer);
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
    public InteractionResult harvest(@Nonnull Consumer<ItemStack> consumer, LivingEntity entity) {
        if (this.getLevel() != null && this.canBeHarvested(entity)) {
            if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Pre(this, entity))) {
                CropHelper.executePlantHarvestRolls(this, consumer);
                this.setGrowthStage(this.getPlant().getGrowthStageAfterHarvest());
                this.getPlant().onHarvest(this, entity);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Post(this, entity));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity) {
        return this.getWeeds().isWeed();
    }

    @Override
    public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
        if(this.getLevel() == null || this.getLevel().isClientSide()) {
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
            if(this.getWeeds().getGrowthStages().contains(stage) && this.checkGrowthSpace(this.getWeeds(), stage)) {
                this.weedGrowth.set(stage);
                this.getWeeds().onGrowthTick(this);
                this.handlePlantUpdate();
                return true;
            }
            return false;
        } else if(weed.getGrowthStages().contains(stage) && this.checkGrowthSpace(this.getWeeds(), stage)) {
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
        this.handlePlantUpdate();
    }

    @Override
    public boolean removeWeed() {
        if(this.hasWeeds()) {
            this.weed.set(NO_WEED);
            this.weedGrowth.set(NO_GROWTH);
            this.handlePlantUpdate();
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsGenome(@Nonnull IAgriGenome genome) {
        return !this.hasGenome() && !this.isCrossCrop();
    }

    @Override
    public boolean spawnGenome(@Nonnull IAgriGenome genome) {
        if(!this.hasPlant() && !this.isCrossCrop() && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Pre(this, genome))) {
            this.setGenomeImpl(genome);
            this.getPlant().onSpawned(this);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Post(this, genome));
            return true;
        }
        return false;
    }

    @Override
    public boolean plantGenome(@Nonnull IAgriGenome genome) {
        return this.plantGenome(genome, null);
    }

    @Override
    public boolean plantGenome(@Nonnull IAgriGenome genome, @Nullable LivingEntity entity) {
        if (this.acceptsGenome(genome) && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Pre(this, genome, entity))) {
            this.setGenomeImpl(genome);
            if(this.getLevel() != null) {
                CropHelper.playPlantingSound(this.getLevel(), this.getBlockPos(), (entity instanceof Player) ? (Player) entity : null);
            }
            this.getPlant().onPlanted(this, entity);
            MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Post(this, genome, entity));
            return true;
        }
        return false;
    }

    protected void setGenomeImpl(@Nonnull IAgriGenome genome) {
        this.genome.set(Optional.of(genome));
        this.growth.set(genome.getPlant().getInitialGrowthStage());
        this.handlePlantUpdate();
    }

    @Override
    public boolean removeGenome() {
        if(this.hasGenome()) {
            this.getPlant().onRemoved(this);
            this.genome.set(Optional.empty());
            this.handlePlantUpdate();
            if(this.getLevel() != null && !this.getLevel().isClientSide()) {
                this.getLevel().setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean hasGenome() {
        return this.hasPlant();
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundTag tag) {
        // No need to write anything since everything is covered by the AutoSyncedFields
    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {
        // A cache update will be required though (either on the client, or on the server after being loaded)
        this.needsCaching = true;
    }

    protected void handlePlantUpdate()  {
        if(this.getLevel() != null) {
            BlockState oldState = this.getBlockState();
            BlockState newState = oldState;
            // Update brightness
            int brightness = this.getPlant().getBrightness(this);
            if (BlockCrop.LIGHT.fetch(newState) != brightness) {
                newState = BlockCrop.LIGHT.apply(newState, brightness);
            }
            // Update plant state
            boolean plant = this.hasPlant() || this.hasWeeds();
            if (BlockCrop.STATE.fetch(newState).hasPlant() != plant) {
                newState = BlockCrop.STATE.apply(newState, BlockCrop.CropState.PLANT);
            }
            // Set block state if necessary
            if(newState != oldState) {
                this.getLevel().setBlock(this.getBlockPos(), newState, 3);
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CropCapability.getCapability()) {
            return this.cropCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onChunkUnloaded() {
        this.requirement.flush();
        super.onChunkUnloaded();
    }



    @Override
    public void setRemoved() {
        this.requirement.flush();
        super.setRemoved();
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
    public void addDisplayInfo(@Nonnull Consumer<Component> consumer) {
        CropHelper.addDisplayInfo(this, this.requirement, consumer);
    }

    @Nonnull
    @Override
    public final ModelDataMap getModelData() {
        return this.data;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        LazyOptional<T> fromPlugins = PluginHandler.getCropCapability(cap, this);
        if(fromPlugins.isPresent()) {
            return fromPlugins;
        } else {
            return super.getCapability(cap);
        }
    }
}
