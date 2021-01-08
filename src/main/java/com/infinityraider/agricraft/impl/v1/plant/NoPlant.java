package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class NoPlant implements IAgriPlant {
    private static final IAgriPlant INSTANCE = new NoPlant();

    public static IAgriPlant getInstance() {
        return INSTANCE;
    }

    private final String id;
    private final Set<IAgriGrowthStage> stages;

    private NoPlant() {
        this.id = "none";
        this.stages = ImmutableSet.of(this.getInitialGrowthStage());
    }

    @Override
    public final boolean isPlant() {
        return false;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getSeeds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return false;
    }

    @Override
    public double getSpreadChance(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Override
    public double getGrowthChanceBase(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Override
    public double getGrowthChanceBonus(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Override
    public double getSeedDropChanceBase(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Override
    public double getSeedDropChanceBonus(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return NoGrowth.getInstance();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getGrowthStageAfterHarvest() {
        return this.getInitialGrowthStage();
    }

    @Nonnull
    @Override
    public Collection<IAgriGrowthStage> getGrowthStages() {
        return stages;
    }

    @Override
    public Optional<BlockState> asBlockState(IAgriGrowthStage stage) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getInformation(IAgriGrowthStage stage) {
        return "";
    }

    @Nonnull
    @Override
    public ItemStack getSeed() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public Set<IGrowCondition> getGrowConditions(IAgriGrowthStage stage) {
        return ImmutableSet.of();
    }

    @Override
    public void getAllPossibleProducts(IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> products) {
        //NOPE
    }

    @Override
    public void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand) {
        //NOPE
    }

    @Override
    public boolean allowsCloning(IAgriGrowthStage stage) {
        return false;
    }

    @Override
    public IAgriGene<IAgriPlant> gene() {
        return GeneSpecies.getInstance();
    }

    @Override
    public IAgriPlant trait() {
        return this;
    }

    @Override
    public boolean isDominant(IAllel<IAgriPlant> other) {
        return false;
    }

    @Override
    @Nonnull
    public CompoundNBT writeToNBT() {
        return new CompoundNBT();
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(IAgriGrowthStage stage) {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return ImmutableList.of();
    }
}
