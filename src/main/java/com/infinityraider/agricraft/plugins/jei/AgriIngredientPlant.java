package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@OnlyIn(Dist.CLIENT)
public class AgriIngredientPlant {
    public static final IIngredientType<IAgriPlant> TYPE = () -> IAgriPlant.class;

    private static final IIngredientHelper<IAgriPlant> HELPER = new IIngredientHelper<IAgriPlant>() {
        @Override
        public IAgriPlant getMatch(Iterable<IAgriPlant> iterable, @Nonnull IAgriPlant plant) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .filter(aPlant -> aPlant == plant)
                    .findFirst().orElse(plant);
        }

        @Override
        public String getDisplayName(IAgriPlant plant) {
            return plant.getPlantName().getString();
        }

        @Override
        public String getUniqueId(IAgriPlant plant) {
            return plant.getId();
        }

        @Nonnull
        @Override
        public String getModId(IAgriPlant plant) {
            return plant.getSeedModel().getNamespace();
        }

        @Override
        public String getResourceId(IAgriPlant plant) {
            return plant.getId();
        }

        @Nonnull
        @Override
        public IAgriPlant copyIngredient(@Nonnull IAgriPlant plant) {
            return plant;
        }

        @Override
        public String getErrorInfo(@Nullable IAgriPlant plant) {
            return plant == null ? "UNKNOWN PLANT" : plant.getId();
        }
    };

    public static final Renderer RENDERER = new Renderer();

    public static void register(IModIngredientRegistration registration) {
        registration.register(TYPE, AgriApi.getPlantRegistry().stream().collect(Collectors.toList()), HELPER, RENDERER);
    }

    public static class Renderer implements IAgriIngredientRenderer<IAgriPlant> {
        private final Map<IAgriPlant, IAgriGrowthStage> stageMap = new IdentityHashMap<>();

        public void useGrowthStageForNextRenderCall(IAgriPlant plant, IAgriGrowthStage stage) {
            // It is an ugly hack, but JEI does not really provide anything to handle this elegantly
            stageMap.put(plant, stage);
        }

        @Override
        public void render(@Nonnull MatrixStack transform, int x, int y, @Nullable IAgriPlant plant) {
            if(plant == null) {
                return;
            }
            Optional.ofNullable(stageMap.remove(plant)).map(stage -> {
                    this.renderStage(transform, x, y, plant, stage);
                    return true;
            }).orElseGet(() ->  {
                plant.getGrowthStages().stream().filter(IAgriGrowthStage::isMature).findFirst().ifPresent(stage ->
                    this.renderStage(transform, x, y, plant, stage));
                return false;
            });
        }

        private void renderStage(@Nonnull MatrixStack transform, int x, int y, IAgriPlant plant, IAgriGrowthStage stage) {
            List<ResourceLocation> tex = plant.getTexturesFor(stage);
            if(tex.size() > 0) {
                this.bindTextureAtlas();
                Screen.blit(transform, x, y, 0, 16, 16, this.getSprite(tex.get(0)));
                MinecraftForge.EVENT_BUS.post(new PostJeiRenderStageEvent(transform, x, y, plant, stage, this));
            }
        }

        @Nonnull
        @Override
        public List<ITextComponent> getTooltip(IAgriPlant plant, @Nonnull ITooltipFlag iTooltipFlag) {
            List<ITextComponent> tooltip = Lists.newArrayList();
            plant.addTooltip(tooltip::add);
            return tooltip;
        }
    }
}
