package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.plant.AgriWeed;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class JsonWeed implements IAgriWeed {
    private final AgriWeed weed;

    private final ITextComponent name;
    private final ITextComponent description;
    private final List<IAgriGrowthStage> growthStages;

    public JsonWeed(AgriWeed weed) {
        this.weed = weed;
        this.name = new TranslationTextComponent(weed.getWeedLangKey());
        this.description = new TranslationTextComponent(weed.getDescLangKey());
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.weed.getGrowthStages());
    }

    @Nonnull
    @Override
    public ITextComponent getWeedName() {
        return this.name;
    }

    @Override
    public double spawnChance(IAgriCrop crop) {
        return this.weed.getSpawnChance();
    }

    @Override
    public double getGrowthChance(IAgriGrowthStage growthStage) {
        return this.weed.getGrowthChance();
    }

    @Override
    public boolean isAggressive() {
        return this.weed.isAggressive();
    }

    @Override
    public boolean isLethal() {
        return weed.isLethal();
    }

    @Override
    public void onRake(@Nonnull IAgriGrowthStage stage, @Nonnull Consumer<ItemStack> consumer, @Nonnull Random rand, @Nullable LivingEntity entity) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if (index < 0) {
            return;
        }
        if (AgriCraft.instance.getConfig().rakingDropsItems()) {
            final double probability = (index + 0.0) / (this.weed.getGrowthStages() - 1.0);
            if(rand.nextDouble() < probability) {
                this.weed.getRakeDrops().getRandom(rand).stream()
                        .map(p -> p.convertSingle(ItemStack.class, rand))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(consumer);
            }
        }
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
    public double getPlantHeight(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(index < 0 || index >= this.weed.getGrowthStages()) {
            return 0;
        }
        return this.weed.getGrowthStageHeight(index)*this.weed.getTexture().getRenderType().getHeightModifier();
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(this.description);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        final int height = this.weed.getGrowthStageHeight(index);
        ImmutableList.Builder<BakedQuad> listBuilder = new ImmutableList.Builder<>();
        int layer = 0;
        while((16*layer) < height) {
            ResourceLocation rl = layer == 0
                    ? new ResourceLocation(this.weed.getTexture().getPlantTexture(index))
                    : new ResourceLocation(this.weed.getTexture().getPlantTexture(this.weed.getGrowthStages() + layer - 1));
            switch (this.weed.getTexture().getRenderType()) {
                case HASH:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForHashPattern(face, rl, layer));
                    break;
                case CROSS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForCrossPattern(face, rl, layer));
                    break;
                case PLUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForPlusPattern(face, rl, layer));
                    break;
                case RHOMBUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForRhombusPattern(face, rl, layer));
                    break;
                default:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForDefaultPattern(face, rl, layer));
                    break;
            }
            layer++;
        }
        return listBuilder.build();
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
