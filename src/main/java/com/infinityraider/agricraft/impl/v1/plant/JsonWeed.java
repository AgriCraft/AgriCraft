package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.templates.AgriWeed;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JsonWeed implements IAgriWeed {
    private final AgriWeed weed;

    private final Component name;
    private final Component description;
    private final List<IAgriGrowthStage> growthStages;

    public JsonWeed(AgriWeed weed) {
        this.weed = weed;
        this.name = new TranslatableComponent(weed.getWeedLangKey());
        this.description = new TranslatableComponent(weed.getDescLangKey());
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.weed.getGrowthStages());
    }

    @Nonnull
    @Override
    public Component getWeedName() {
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
        return this.weed.getGrowthStageHeight(index);
    }

    @Override
    public void addTooltip(Consumer<Component> consumer) {
        consumer.accept(this.description);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(this.weed.getTexture().useModels()) {
            ResourceLocation rl = new ResourceLocation(this.weed.getTexture().getPlantModel(index));
            return AgriPlantQuadGenerator.getInstance().fetchQuads(rl);
        } else {
            List<ResourceLocation> textures = Arrays.stream(weed.getTexture().getPlantTextures(index))
                    .map(ResourceLocation::new)
                    .collect(Collectors.toList());
            return AgriPlantQuadGenerator.getInstance().bakeQuads(this, stage, this.weed.getTexture().getRenderType(),
                    face, textures);
        }
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return Arrays.stream(this.weed.getTexture().getPlantTextures(IncrementalGrowthLogic.getGrowthIndex(stage)))
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
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
