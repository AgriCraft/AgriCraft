package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

public final class NoWeed implements IAgriWeed {
    private static final IAgriWeed INSTANCE = new NoWeed();

    public static IAgriWeed getInstance() {
        return INSTANCE;
    }

    private final String id = "none";
    private final Set<IAgriGrowthStage> stages = ImmutableSet.of(this.getInitialGrowthStage());

    private NoWeed() {}

    @Override
    public final boolean isWeed() {
        return false;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }


    @Nonnull
    @Override
    public Component getWeedName() {
        return AgriToolTips.UNKNOWN;
    }

    @Override
    public double spawnChance(IAgriCrop crop) {
        return 0;
    }

    @Override
    public double getGrowthChance(IAgriGrowthStage growthStage) {
        return 0;
    }

    @Override
    public boolean isAggressive() {
        return false;
    }

    @Override
    public boolean isLethal() {
        return false;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return NoGrowth.getInstance();
    }

    @Nonnull
    @Override
    public Set<IAgriGrowthStage> getGrowthStages() {
        return this.stages;
    }

    @Override
    public double getPlantHeight(IAgriGrowthStage stage) {
        return 0;
    }

    @Override
    public void onRake(@Nonnull IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> consumer, @Nonnull Random rand, @Nullable LivingEntity entity) {
        //NOPE
    }

    private final String info = "Damnations! This is not a weed";
    private final Component tooltip = new TextComponent(this.info);

    @Override
    public void addTooltip(Consumer<Component> consumer) {
        consumer.accept(tooltip);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return ImmutableList.of();
    }
}
