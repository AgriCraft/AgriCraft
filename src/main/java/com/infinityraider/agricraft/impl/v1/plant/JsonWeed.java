package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.plant.AgriRenderType;
import com.agricraft.agricore.plant.AgriWeed;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class JsonWeed implements IAgriWeed {
    private final AgriWeed weed;

    private final List<IAgriGrowthStage> growthStages;

    public JsonWeed(AgriWeed weed) {
        this.weed = weed;
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.weed.getGrowthStages());
    }

    @Override
    public double spawnChance(IAgriCrop crop) {
        return 0;
    }

    @Override
    public void onRake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity) {

    }

    @Nonnull
    @Override
    public String getId() {
        return this.weed.getId();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return this.growthStages.get(0);
    }

    @Nonnull
    @Override
    public Collection<IAgriGrowthStage> getGrowthStages() {
        return this.growthStages;
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(new StringTextComponent(this.weed.getWeedName()));
        consumer.accept(new StringTextComponent(this.weed.getDescription().toString()));
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuads(Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        ResourceLocation rl = new ResourceLocation(this.weed.getTexture().getPlantTexture(index));
        if (this.weed.getTexture().getRenderType() == AgriRenderType.CROSS) {
            return AgriApi.getPlantQuadGenerator().bakeQuadsForCrossPattern(face, rl);
        }
        if (this.weed.getTexture().getRenderType() == AgriRenderType.HASH) {
            return AgriApi.getPlantQuadGenerator().bakeQuadsForHashPattern(face, rl);
        }
        return AgriApi.getPlantQuadGenerator().bakeQuadsForDefaultPattern(face, rl);
    }

    @Nullable
    protected ResourceLocation getTextureFor(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        return index < 0 ? null : new ResourceLocation(this.weed.getTexture().getPlantTexture(index));
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        ResourceLocation rl = this.getTextureFor(stage);
        return rl == null ? ImmutableList.of() : ImmutableList.of(rl);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IAgriWeed) && (this.getId().equals(((IAgriWeed) obj).getId()));
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
