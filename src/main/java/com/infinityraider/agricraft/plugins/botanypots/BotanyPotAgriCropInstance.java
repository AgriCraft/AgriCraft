package com.infinityraider.agricraft.plugins.botanypots;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.requirement.AgriGrowthRequirement;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class BotanyPotAgriCropInstance implements CropCapability.Instance<TileEntityBotanyPot, BotanyPotAgriCropInstance.Impl> {
    private static final BotanyPotAgriCropInstance INSTANCE = new BotanyPotAgriCropInstance();

    public static BotanyPotAgriCropInstance getInstance() {
        return INSTANCE;
    }

    private BotanyPotAgriCropInstance() {}

    @Override
    public Class<TileEntityBotanyPot> getCarrierClass() {
        return TileEntityBotanyPot.class;
    }

    @Override
    public Class<Impl> getCropClass() {
        return Impl.class;
    }

    @Override
    public Impl createCropFor(TileEntityBotanyPot tile) {
        return new Impl(tile);
    }

    @Override
    public void writeToNBT(CompoundNBT tag, Impl crop) {
        tag.putString(AgriNBT.GROWTH, crop.stage.getId());
        tag.putString(AgriNBT.WEED_GROWTH, crop.next.getId());
    }

    @Override
    public void readFromNBT(CompoundNBT tag, Impl crop) {
        if(tag.contains(AgriNBT.GROWTH) && tag.contains(AgriNBT.WEED_GROWTH)) {
            crop.stage = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NoGrowth.getInstance());
            crop.next = crop.stage.isGrowthStage()
                    ? AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NoGrowth.getInstance())
                    : crop.stage;
        }
    }

    // TODO: when the events come to botany pots: cache genome and implement callbacks, potentially implement weeds as well
    public static class Impl implements IAgriCrop {
        // Tile Entity instance
        private final TileEntityBotanyPot pot;

        // Growth stage cache
        private IAgriGrowthStage stage;
        private IAgriGrowthStage next;

        private Impl(TileEntityBotanyPot pot) {
            this.pot = pot;
            this.stage = NoGrowth.getInstance();
            this.next = NoGrowth.getInstance();
        }

        @Override
        public TileEntityBotanyPot asTile() {
            return this.pot;
        }

        @Override
        public boolean isValid() {
            return !this.asTile().isRemoved() && this.asTile().getCrop() instanceof AgriCropInfo;
        }

        @Nonnull
        @Override
        public BlockPos getPosition() {
            return this.asTile().getPos();
        }

        @Nonnull
        @Override
        public BlockState getBlockState() {
            return this.asTile().getBlockState();
        }

        @Nonnull
        @Override
        public FluidState getFluidState() {
            return this.getBlockState().getFluidState();
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getGrowthStage() {
            World world = this.world();
            if(world == null) {
                return NoGrowth.getInstance();
            }
            if(this.hasPlant()) {
                if (this.stage.isGrowthStage() && this.next.isGrowthStage()) {
                    float progress = this.asTile().getGrowthPercent();
                    if(progress >= this.next.growthPercentage() && !this.stage.isFinal()) {
                        this.stage = this.next;
                        this.next = this.stage.getNextStage(this, world.getRandom());
                    }
                } else {
                    float progress = this.asTile().getGrowthPercent();
                    this.stage = this.getPlant().getGrowthStages().stream()
                            .sorted(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage))
                            .filter(stage -> stage.growthPercentage() >= progress)
                            .findFirst().orElse(this.getPlant().getFinalStage());
                    this.next = this.stage.getNextStage(this, world.getRandom());
                }
            } else {
                this.stage = NoGrowth.getInstance();
                this.next = NoGrowth.getInstance();
            }
            return this.stage;
        }

        @Override
        public boolean setGrowthStage(@Nonnull IAgriGrowthStage stage) {
            return false;
        }

        @Override
        public boolean hasCropSticks() {
            // No crop sticks on botany pots (for now)
            return false;
        }

        @Override
        public boolean isCrossCrop() {
            // No crop sticks on botany pots (for now)
            return false;
        }

        @Override
        public boolean setCrossCrop(boolean status) {
            // No crop sticks on botany pots (for now)
            return false;
        }

        @Override
        public IAgriGrowthResponse getFertilityResponse() {
            return this.getPlant().getGrowthRequirement(this.getGrowthStage()).check(this);
        }

        @Override
        public boolean isMature() {
            return this.asTile().canHarvest();
        }

        @Override
        public boolean isFullyGrown() {
            return this.asTile().canHarvest();
        }

        @Nonnull
        @Override
        public Optional<IAgriSoil> getSoil() {
            // Fetch soil info
            SoilInfo soilInfo = this.asTile().getSoil();
            if(soilInfo == null) {
                return Optional.empty();
            }
            // Correction for farmland
            Block block = soilInfo.getRenderState().getState().getBlock();
            if(block == Blocks.DIRT) {
                block = Blocks.FARMLAND;
            }
            // Return soil
            return AgriApi.getSoilRegistry().valueOf(block);
        }

        @Override
        public void breakCrop(@Nullable LivingEntity entity) {
            if(this.hasPlant()) {
                IAgriPlant plant = this.getPlant();
                IAgriWeed weed = this.getWeeds();
                if (!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Pre(this, entity)) && this.removeGenome()) {
                    plant.onBroken(this, entity);
                    weed.onBroken(this, entity);
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Post(this, entity));
                }
            }
        }

        @Nonnull
        @Override
        public Stream<IAgriCrop> streamNeighbours() {
            return Stream.empty();
        }

        @Override
        public void dropItem(ItemStack item) {
            World world = this.world();
            if(world != null) {
                BlockBotanyPot.dropItem(item, world, this.getPosition());
            }
        }

        @Override
        public boolean canBeHarvested(@Nullable LivingEntity entity) {
            return this.hasPlant() && this.isMature() && this.getPlant().allowsHarvest(this.getGrowthStage(), entity);
        }

        @Nonnull
        @Override
        public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            World world = this.world();
            CropInfo crop = this.asTile().getCrop();
            if(world != null && crop instanceof AgriCropInfo && this.canBeHarvested(entity)) {
                if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Pre(this, entity))) {
                    this.asTile().onCropHarvest();
                    this.asTile().resetGrowthTime();
                    for (final ItemStack stack : BotanyPotHelper.getHarvestStacks(world, crop)) {
                        this.dropItem(stack);
                    }
                    this.getPlant().onHarvest(this, entity);
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Post(this, entity));
                    return ActionResultType.SUCCESS;
                }
            }
            return ActionResultType.PASS;
        }

        @Override
        public boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity) {
            // No weeds on botany pots (for now)
            return false;
        }

        @Override
        public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            // No weeds on botany pots (for now)
            return false;
        }

        @Override
        public boolean acceptsFertilizer(@Nonnull IAgriFertilizer fertilizer) {
            // This is governed by the pot logic
            return false;
        }

        @Override
        public void onApplyFertilizer(@Nonnull IAgriFertilizer fertilizer, @Nonnull Random rand) {
            // This is governed by the pot logic
        }

        @Override
        public void applyGrowthTick() {
            // This is governed by the pot logic
        }

        @Override
        public void addDisplayInfo(@Nonnull Consumer<ITextComponent> consumer) {
            // Validate
            Preconditions.checkNotNull(consumer);

            // Add plant information
            if (this.hasPlant()) {
                //Add the plant data.
                consumer.accept(AgriToolTips.getPlantTooltip(this.getPlant()));
                //Add the stats
                this.getStats().addTooltips(consumer);
                //Add the fertility information.
                this.getSoil().ifPresent(soil -> {
                    // TODO: update this tooltip once actual stats are used
                    IAgriGrowthRequirement req = this.getPlant().getGrowthRequirement(this.getPlant().getInitialGrowthStage());
                    if(!req.getSoilHumidityResponse(soil.getHumidity(), 1).isFertile()) {
                        AgriGrowthRequirement.Tooltips.HUMIDITY_DESCRIPTION.forEach(consumer);
                    }
                    if(!req.getSoilAcidityResponse(soil.getAcidity(), 1).isFertile()) {
                        AgriGrowthRequirement.Tooltips.ACIDITY_DESCRIPTION.forEach(consumer);
                    }
                    if(!req.getSoilNutrientsResponse(soil.getNutrients(), 1).isFertile()) {
                        AgriGrowthRequirement.Tooltips.NUTRIENT_DESCRIPTION.forEach(consumer);
                    }
                });
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

        @Override
        public boolean hasPlant() {
            return this.asTile().getCrop() instanceof AgriCropInfo
                    && ((AgriCropInfo) this.asTile().getCrop()).getPlant().isPlant();
        }

        @Nonnull
        @Override
        public IAgriPlant getPlant() {
            return this.hasPlant()
                    ? ((AgriCropInfo) this.asTile().getCrop()).getPlant()
                    : NoPlant.getInstance();
        }

        @Override
        public boolean hasWeeds() {
            // No weeds on botany pots (for now)
            return false;
        }

        @Nonnull
        @Override
        public IAgriWeed getWeeds() {
            // No weeds on botany pots (for now)
            return NoWeed.getInstance();
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getWeedGrowthStage() {
            // No weeds on botany pots (for now)
            return NoGrowth.getInstance();
        }

        @Override
        public boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
            // No weeds on botany pots (for now)
            return false;
        }

        @Override
        public boolean removeWeed() {
            // No weeds on botany pots (for now)
            return false;
        }

        @Override
        public boolean acceptsGenome(@Nonnull IAgriGenome genome) {
            return this.asTile().getCrop() == null && this.asTile().getSoil() != null;
        }

        @Override
        public boolean spawnGenome(@Nonnull IAgriGenome genome) {
            if (this.asTile().getCrop() != null) {
                return false;
            }
            SoilInfo soil = this.asTile().getSoil();
            if (soil == null) {
                return false;
            }
            ItemStack seed = genome.toSeedStack();
            CropInfo crop = BotanyPotHelper.getCropForItem(seed);
            if (crop != null && BotanyPotHelper.isSoilValidForCrop(soil, crop) && this.asTile().canSetCrop(crop)) {
                if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Pre(this, genome))) {
                    this.asTile().setCrop(crop, seed);
                    this.getPlant().onSpawned(this);
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Plant.Post(this, genome));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean plantGenome(@Nonnull IAgriGenome genome) {
            return this.plantGenome(genome, null);
        }

        @Override
        public boolean plantGenome(@Nonnull IAgriGenome genome, @Nullable LivingEntity entity) {
            if (this.asTile().getCrop() != null) {
                return false;
            }
            SoilInfo soil = this.asTile().getSoil();
            if (soil == null) {
                return false;
            }
            ItemStack seedStack = genome.toSeedStack();
            CropInfo crop = BotanyPotHelper.getCropForItem(seedStack);
            if (crop != null && BotanyPotHelper.isSoilValidForCrop(soil, crop) && this.asTile().canSetCrop(crop)) {
                if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Plant.Pre(this, genome, entity))) {
                    this.asTile().setCrop(crop, seedStack);
                    this.getPlant().onSpawned(this);
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Plant.Plant.Post(this, genome, entity));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean removeGenome() {
            if (this.asTile().getCrop() != null) {
                ItemStack seedStack = AgriApi.getGenomeAdapterizer().valueOf(this.asTile().getCropStack())
                        .map(IAgriGenome::toSeedStack)
                        .orElse(this.asTile().getCropStack());
                if (!seedStack.isEmpty() && this.asTile().canSetCrop(null)) {
                    this.asTile().setCrop(null, ItemStack.EMPTY);
                    this.dropItem(seedStack);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean hasGenome() {
            return this.hasPlant();
        }

        @Nonnull
        @Override
        public Optional<IAgriGenome> getGenome() {
            ItemStack stack = this.asTile().getCropStack();
            if(!(stack.getItem() instanceof IAgriSeedItem)) {
                return Optional.empty();
            }
            return ((IAgriSeedItem) stack.getItem()).getGenome(stack);
        }

        @Nonnull
        @Override
        public IAgriStatsMap getStats() {
            return this.getGenome().map(IAgriStatProvider::getStats).orElse(NoStats.getInstance());
        }
    }
}
