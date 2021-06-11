package com.infinityraider.agricraft.plugins.botanypots;

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
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.requirement.RequirementCache;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.util.CropHelper;
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
        tag.putString(AgriNBT.PLANT, crop.nextStage.getId());
        tag.putString(AgriNBT.WEED, crop.weed.getId());
        tag.putString(AgriNBT.WEED_GROWTH, crop.weedStage.getId());
        tag.putString(AgriNBT.CONTENTS, crop.nextWeedStage.getId());
        tag.putInt(AgriNBT.LEVEL, crop.weedCounter);
    }

    @Override
    public void readFromNBT(CompoundNBT tag, Impl crop) {
        if(tag.contains(AgriNBT.GROWTH) && tag.contains(AgriNBT.PLANT)) {
            crop.stage = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NoGrowth.getInstance());
            crop.nextStage = crop.stage.isGrowthStage()
                    ? AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NoGrowth.getInstance())
                    : crop.stage;
        }
        if(tag.contains(AgriNBT.GROWTH) && tag.contains(AgriNBT.PLANT)) {
            crop.weed = AgriApi.getWeedRegistry().get(tag.getString(AgriNBT.WEED)).orElse(NoWeed.getInstance());
            if(crop.weed.isWeed()) {
                crop.weedStage = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NoGrowth.getInstance());
                crop.nextWeedStage = crop.weedStage.isGrowthStage()
                        ? AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.CONTENTS)).orElse(NoGrowth.getInstance())
                        : crop.weedStage;
                crop.weedCounter = tag.contains(AgriNBT.LEVEL) ? tag.getInt(AgriNBT.WEED) : 0;
            } else {
                crop.weedStage = NoGrowth.getInstance();
                crop.nextWeedStage = NoGrowth.getInstance();
                crop.weedCounter = 0;
            }
        }
    }

    public static class Impl implements IAgriCrop {
        private static final int WEED_GROWTH_TICKS = 1000;

        // Tile Entity instance
        private final TileEntityBotanyPot pot;

        // Growth stage cache
        private IAgriGrowthStage stage;
        private IAgriGrowthStage nextStage;

        // Weeds
        private IAgriWeed weed;
        private IAgriGrowthStage weedStage;
        private IAgriGrowthStage nextWeedStage;
        private int weedCounter;

        // Requirement cache
        private final RequirementCache requirement;

        private Impl(TileEntityBotanyPot pot) {
            this.pot = pot;
            this.stage = NoGrowth.getInstance();
            this.nextStage = NoGrowth.getInstance();
            this.weed = NoWeed.getInstance();
            this.weedStage = NoGrowth.getInstance();
            this.nextWeedStage = NoGrowth.getInstance();
            this.requirement = RequirementCache.create(this);
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
                if (this.stage.isGrowthStage() && this.nextStage.isGrowthStage()) {
                    float progress = this.asTile().getGrowthPercent();
                    if(progress >= this.nextStage.growthPercentage() && !this.stage.isFinal()) {
                        this.stage = this.nextStage;
                        this.nextStage = this.stage.getNextStage(this, world.getRandom());
                    }
                } else {
                    float progress = this.asTile().getGrowthPercent();
                    this.stage = this.getPlant().getGrowthStages().stream()
                            .sorted(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage))
                            .filter(stage -> stage.growthPercentage() >= progress)
                            .findFirst().orElse(this.getPlant().getFinalStage());
                    this.nextStage = this.stage.getNextStage(this, world.getRandom());
                }
            } else {
                this.stage = NoGrowth.getInstance();
                this.nextStage = NoGrowth.getInstance();
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
            if(this.world() == null) {
                return IAgriGrowthResponse.INFERTILE;
            }
            return this.requirement.check();
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
            return this.hasPlant()
                    && this.isMature()
                    && this.getPlant().allowsHarvest(this.getGrowthStage(), entity)
                    && !MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Pre(this, entity));
        }

        @Nonnull
        @Override
        public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            World world = this.world();
            CropInfo crop = this.asTile().getCrop();
            if(world != null && crop instanceof AgriCropInfo && this.canBeHarvested(entity)) {
                this.getPlant().getHarvestProducts(consumer, this.getGrowthStage(), this.getStats(), world.getRandom());
                this.getPlant().onHarvest(this, entity);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Harvest.Post(this, entity));
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }

        @Override
        public boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity) {
            return this.hasWeeds();
        }

        @Override
        public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            World world = this.world();
            if(world == null || world.isRemote()) {
                return false;
            }
            IAgriWeed weed = this.getWeeds();
            if (weed.isWeed()) {
                IAgriGrowthStage stage = this.getWeedGrowthStage();
                this.setWeed(NoWeed.getInstance(), NoGrowth.getInstance());
                weed.onRake(stage, consumer, world.getRandom(), entity);
                return true;
            }
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
            CropHelper.addDisplayInfo(this, this.requirement, consumer);
        }

        @Override
        public boolean hasPlant() {
            CropInfo info = this.asTile().getCrop();
            return info instanceof AgriCropInfo && ((AgriCropInfo) info).getPlant().isPlant();
        }

        @Nonnull
        @Override
        public IAgriPlant getPlant() {
            CropInfo info = this.asTile().getCrop();
            return info instanceof AgriCropInfo ? ((AgriCropInfo) info).getPlant() : NoPlant.getInstance();
        }

        @Override
        public boolean hasWeeds() {
            return this.weed.isWeed();
        }

        @Nonnull
        @Override
        public IAgriWeed getWeeds() {
            return this.weed;
        }

        public boolean incrementWeedCounter(int amount) {
            if(this.hasWeeds()) {
                this.weedCounter += amount;
                this.weedCounter = Math.min(WEED_GROWTH_TICKS, Math.max(0, this.weedCounter));
                if(weedCounter == 0) {
                    this.removeWeed();
                }
                return this.getWeedGrowthStage().isFinal();
            }
            return false;
        }

        public float getWeedGrowthPercent() {
            return ((float) this.weedCounter) / WEED_GROWTH_TICKS;
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getWeedGrowthStage() {
            World world = this.world();
            if(world == null) {
                return NoGrowth.getInstance();
            }
            if(this.hasWeeds()) {
                if (this.weedStage.isGrowthStage() && this.nextWeedStage.isGrowthStage()) {
                    float progress = this.getWeedGrowthPercent();
                    if(progress >= this.nextWeedStage.growthPercentage() && !this.weedStage.isFinal()) {
                        this.weedStage = this.nextWeedStage;
                        this.nextWeedStage = this.weedStage.getNextStage(this, world.getRandom());
                    }
                } else {
                    float progress = this.getWeedGrowthPercent();
                    this.weedStage = this.getWeeds().getGrowthStages().stream()
                            .sorted(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage))
                            .filter(stage -> stage.growthPercentage() >= progress)
                            .findFirst().orElse(this.getWeeds().getFinalStage());
                    this.nextWeedStage = this.weedStage.getNextStage(this, world.getRandom());
                }
            } else {
                this.weedStage = NoGrowth.getInstance();
                this.nextWeedStage = NoGrowth.getInstance();
            }
            return this.weedStage;
        }

        @Override
        public boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
            World world = this.world();
            if(world == null) {
                return false;
            }
            if(this.getWeeds().equals(weed)) {
                if(this.weedStage.equals(stage)) {
                    return false;
                }
                if(this.getWeeds().getGrowthStages().contains(stage)) {
                    this.weedStage = stage;
                    this.getWeeds().onGrowthTick(this);
                    this.nextWeedStage = this.weedStage.getNextStage(this, world.getRandom());
                    this.weedCounter = (int) (WEED_GROWTH_TICKS*stage.growthPercentage());
                    return true;
                }
                return false;
            } else if(weed.getGrowthStages().contains(stage)) {
                if(!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Weed.Pre(this, weed))) {
                    this.weed = weed;
                    this.weedStage = stage;
                    this.nextWeedStage = this.weedStage.getNextStage(this, world.getRandom());
                    this.weedCounter = (int) (WEED_GROWTH_TICKS * stage.growthPercentage());
                    MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Spawn.Weed.Post(this, weed));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean removeWeed() {
            if(this.hasWeeds()) {
                this.weed = NoWeed.getInstance();
                this.weedStage = NoGrowth.getInstance();
                this.nextWeedStage = NoGrowth.getInstance();
                this.weedCounter = 0;
                return true;
            }
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
            if (BotanyPotHelper.isSoilValidForCrop(soil, crop) && this.asTile().canSetCrop(crop)) {
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
            if (BotanyPotHelper.isSoilValidForCrop(soil, crop) && this.asTile().canSetCrop(crop)) {
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
