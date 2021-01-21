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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.StreamSupport;

@OnlyIn(Dist.CLIENT)
public class AgriPlantIngredient {
    public static final IIngredientType<IAgriPlant> TYPE = () -> IAgriPlant.class;

    private static final IIngredientHelper<IAgriPlant> HELPER = new IIngredientHelper<IAgriPlant>() {
        @Nullable
        @Override
        public IAgriPlant getMatch(Iterable<IAgriPlant> iterable, IAgriPlant plant) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .filter(aPlant -> aPlant == plant)
                    .findFirst().orElse(plant);
        }

        @Override
        public String getDisplayName(IAgriPlant plant) {
            return plant.getId();
        }

        @Override
        public String getUniqueId(IAgriPlant plant) {
            return plant.getId();
        }

        @Override
        public String getModId(IAgriPlant plant) {
            return plant.getSeed().getItem().getRegistryName().getNamespace();
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
            return plant.getId();
        }
    };

    private static final IAgriIngredientRenderer<IAgriPlant> RENDERER = new IAgriIngredientRenderer<IAgriPlant>() {
        @Override
        public void render(@Nonnull MatrixStack transform, int x, int y, @Nullable IAgriPlant plant) {
            if(plant == null) {
                return;
            }
            plant.getGrowthStages().stream().filter(IAgriGrowthStage::isMature).findFirst().ifPresent(stage -> {
                List<ResourceLocation> tex = plant.getTexturesFor(stage);
                if(tex.size() > 0) {
                    this.bindTextureAtlas();
                    Screen.blit(transform, x, y, 0, 16, 16, this.getSprite(tex.get(0)));
                }
            });
        }

        @Override
        public List<ITextComponent> getTooltip(IAgriPlant plant, @Nonnull ITooltipFlag iTooltipFlag) {
            List<ITextComponent> tooltip = Lists.newArrayList();
            plant.addTooltip(tooltip::add);
            return tooltip;
        }
    };

    public static void register(IModIngredientRegistration registration) {
        registration.register(TYPE, AgriApi.getPlantRegistry().all(), HELPER, RENDERER);
    }
}
