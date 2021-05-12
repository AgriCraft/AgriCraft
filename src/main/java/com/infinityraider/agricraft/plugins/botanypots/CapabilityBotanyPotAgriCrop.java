package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.agricraft.impl.v1.stats.NoStats;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CapabilityBotanyPotAgriCrop implements IInfSerializableCapabilityImplementation<TileEntityBotanyPot, CapabilityBotanyPotAgriCrop.Impl> {
    private static final CapabilityBotanyPotAgriCrop INSTANCE = new CapabilityBotanyPotAgriCrop();

    public static CapabilityBotanyPotAgriCrop getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Mods.BOTANY_POTS);

    @CapabilityInject(Impl.class)
    public static final Capability<Impl> CAPABILITY = null;

    private CapabilityBotanyPotAgriCrop() {}

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(TileEntityBotanyPot carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(TileEntityBotanyPot carrier) {
        return new Impl(carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<TileEntityBotanyPot> getCarrierClass() {
        return TileEntityBotanyPot.class;
    }

    // TODO: when the events come to botany pots: cache genome and implement callbacks, potentially implement weeds as well
    public static class Impl implements IAgriCrop, ISerializable {
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
            return this.getPlant().getGrowthRequirement(this.getGrowthStage()).isMet(this);
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
            return AgriApi.getSoilRegistry().valueOf(this.asTile().getSoilStack());
        }

        @Override
        public void breakCrop(@Nullable LivingEntity entity) {
            if (!MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Pre(this, entity))) {
                IAgriPlant plant = this.getPlant();
                IAgriWeed weed = this.getWeeds();
                this.asTile().setCrop(null, ItemStack.EMPTY);
                plant.onBroken(this, entity);
                weed.onBroken(this, entity);
                MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Break.Post(this, entity));
            }
        }

        @Nonnull
        @Override
        public Stream<IAgriCrop> streamNeighbours() {
            return Stream.empty();
        }

        @Override
        public void dropItem(ItemStack item) {

        }

        @Override
        public boolean canBeHarvested(@Nullable LivingEntity entity) {
            return this.isMature();
        }

        @Nonnull
        @Override
        public ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            // This is governed by the pot logic
            return ActionResultType.FAIL;
        }

        @Override
        public boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity) {
            return false;
        }

        @Override
        public boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
            return false;
        }

        @Override
        public boolean acceptsFertilizer(@Nonnull IAgriFertilizer fertilizer) {
            // This is governed by the pot logic
            return false;
        }

        @Override
        public void onApplyFertilizer(@Nonnull IAgriFertilizer fertilizer, @Nonnull Random rand) { }

        @Override
        public void applyGrowthTick() {
            // This is governed by the pot logic
        }

        @Override
        public void addDisplayInfo(@Nonnull Consumer<ITextComponent> consumer) {

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
            return false;
        }

        @Nonnull
        @Override
        public IAgriWeed getWeeds() {
            return NoWeed.getInstance();
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getWeedGrowthStage() {
            return NoGrowth.getInstance();
        }

        @Override
        public boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage) {
            return false;
        }

        @Override
        public boolean removeWeed() {
            return false;
        }

        @Override
        public boolean acceptsSeed(@Nonnull AgriSeed seed) {
            return false;
        }

        @Override
        public boolean setGenome(@Nonnull IAgriGenome genome) {
            return false;
        }

        @Override
        public boolean plantSeed(@Nonnull AgriSeed seed) {
            return false;
        }

        @Override
        public boolean plantSeed(@Nonnull AgriSeed seed, @Nullable LivingEntity entity) {
            return false;
        }

        @Override
        public boolean removeSeed() {
            return false;
        }

        @Override
        public boolean hasSeed() {
            return false;
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

        @Override
        public Optional<AgriSeed> getSeed() {
            IAgriPlant plant = this.getPlant();
            return plant.isPlant() ? Optional.empty() : Optional.of(plant.toAgriSeed());
        }

        @Nonnull
        @Override
        public IAgriStatsMap getStats() {
            return this.getGenome().map(IAgriStatProvider::getStats).orElse(NoStats.getInstance());
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            if(tag.contains(AgriNBT.GROWTH) && tag.contains(AgriNBT.WEED_GROWTH)) {
                this.stage = AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.GROWTH)).orElse(NoGrowth.getInstance());
                this.next = this.stage.isGrowthStage()
                        ? AgriApi.getGrowthStageRegistry().get(tag.getString(AgriNBT.WEED_GROWTH)).orElse(NoGrowth.getInstance())
                        : this.stage;
            }
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putString(AgriNBT.GROWTH, this.stage.getId());
            tag.putString(AgriNBT.WEED_GROWTH, this.next.getId());
            return tag;
        }
    }
}
