package com.infinityraider.agricraft.api.v1.util.mimic;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Utility class, extend to override some selected behaviour of existing plants
 */
public class MimickedPlant implements IAgriPlant {
    private final IAgriPlant original;

    protected MimickedPlant(IAgriPlant original) {
        this.original = original;
    }

    protected final IAgriPlant getOriginal() {
        return this.original;
    }

    @Nonnull
    @Override
    public IFormattableTextComponent getPlantName() {
        return this.getOriginal().getPlantName();
    }

    @Nonnull
    @Override
    public IFormattableTextComponent getSeedName() {
        return this.getOriginal().getSeedName();
    }

    @Override
    public int getTier() {
        return this.getOriginal().getTier();
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return this.getOriginal().isFertilizable(growthStage, fertilizer);
    }

    @Override
    public double getSpreadChance(IAgriGrowthStage growthStage) {
        return this.getOriginal().getSpreadChance(growthStage);
    }

    @Override
    public double getGrowthChanceBase(IAgriGrowthStage growthStage) {
        return this.getOriginal().getGrowthChanceBase(growthStage);
    }

    @Override
    public double getGrowthChanceBonus(IAgriGrowthStage growthStage) {
        return this.getOriginal().getGrowthChanceBonus(growthStage);
    }

    @Override
    public double getSeedDropChanceBase(IAgriGrowthStage growthStage) {
        return this.getOriginal().getSeedDropChanceBase(growthStage);
    }

    @Override
    public double getSeedDropChanceBonus(IAgriGrowthStage growthStage) {
        return this.getOriginal().getSeedDropChanceBonus(growthStage);
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getGrowthStageAfterHarvest() {
        return this.getOriginal().getGrowthStageAfterHarvest();
    }

    @Override
    public Optional<BlockState> asBlockState(IAgriGrowthStage stage) {
        return this.getOriginal().asBlockState(stage);
    }

    @Nonnull
    @Override
    public IFormattableTextComponent getInformation() {
        return this.getOriginal().getInformation();
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        this.getOriginal().addTooltip(consumer);
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getSeedItems() {
        return this.getOriginal().getSeedItems();
    }

    @Nonnull
    @Override
    public IAgriGrowthRequirement getGrowthRequirement(IAgriGrowthStage stage) {
        return this.getOriginal().getGrowthRequirement(stage);
    }

    @Override
    public void getAllPossibleProducts(@Nonnull Consumer<ItemStack> products) {
        this.getOriginal().getAllPossibleProducts(products);
    }

    @Override
    public void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand) {
        this.getOriginal().getHarvestProducts(products, growthStage, stats, rand);
    }

    @Override
    public void getAllPossibleClipProducts(@Nonnull Consumer<ItemStack> products) {
        this.getOriginal().getAllPossibleClipProducts(products);
    }

    @Override
    public void getClipProducts(@Nonnull Consumer<ItemStack> products, @Nonnull ItemStack clipper, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand) {
        this.getOriginal().getClipProducts(products, clipper, growthStage, stats, rand);
    }

    @Override
    public boolean allowsCloning(IAgriGrowthStage stage) {
        return this.getOriginal().allowsCloning(stage);
    }

    @Override
    public void spawnParticles(@Nonnull IAgriCrop crop, Random rand) {
        this.getOriginal().spawnParticles(crop, rand);
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return this.getOriginal().getInitialGrowthStage();
    }

    @Nonnull
    @Override
    public Collection<IAgriGrowthStage> getGrowthStages() {
        return this.getOriginal().getGrowthStages();
    }

    @Override
    public double getPlantHeight(IAgriGrowthStage stage) {
        return this.getOriginal().getPlantHeight(stage);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        return this.getOriginal().bakeQuads(face, stage);
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return this.getOriginal().getTexturesFor(stage);
    }

    @Nonnull
    @Override
    public ResourceLocation getSeedTexture() {
        return this.getOriginal().getSeedTexture();
    }

    @Nonnull
    @Override
    public ResourceLocation getSeedModel() {
        return this.getOriginal().getSeedModel();
    }

    @Nonnull
    @Override
    public String getId() {
        return this.getOriginal().getId();
    }
}
