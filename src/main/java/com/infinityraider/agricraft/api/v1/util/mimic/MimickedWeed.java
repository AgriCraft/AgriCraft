package com.infinityraider.agricraft.api.v1.util.mimic;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Utility class, extend to override some selected behaviour of existing weeds
 */
public class MimickedWeed implements IAgriWeed {
    private final IAgriWeed original;

    protected MimickedWeed(IAgriWeed original) {
        this.original = original;
    }

    protected final IAgriWeed getOriginal() {
        return this.original;
    }

    @Nonnull
    @Override
    public Component getWeedName() {
        return this.getOriginal().getWeedName();
    }

    @Override
    public double spawnChance(IAgriCrop crop) {
        return this.getOriginal().spawnChance(crop);
    }

    @Override
    public double getGrowthChance(IAgriGrowthStage growthStage) {
        return this.getOriginal().getGrowthChance(growthStage);
    }

    @Override
    public boolean isAggressive() {
        return this.getOriginal().isAggressive();
    }

    @Override
    public boolean isLethal() {
        return this.getOriginal().isLethal();
    }

    @Override
    public void onRake(@Nonnull IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> consumer, @Nonnull Random rand, @Nullable LivingEntity entity) {
        this.getOriginal().onRake(stage, consumer, rand, entity);
    }

    @Override
    public void addTooltip(Consumer<Component> consumer) {
        this.getOriginal().addTooltip(consumer);
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
    @OnlyIn(Dist.CLIENT)
    public List<?> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        return this.getOriginal().bakeQuads(face, stage);
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return this.getOriginal().getTexturesFor(stage);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.getOriginal().getId();
    }
}
