package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.crop.NoGrowth;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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
    public void onRake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {
        //NOPE
    }

    private final String info = "Damnations! This is not a weed";
    private final ITextComponent tooltip = new StringTextComponent(this.info);

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(tooltip);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(Direction face, IAgriGrowthStage stage) {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return ImmutableList.of();
    }
}
