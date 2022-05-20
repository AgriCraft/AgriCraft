package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AgriIngredientPlant {
    public static final IIngredientType<IAgriPlant> TYPE = () -> IAgriPlant.class;
    private static final Map<IAgriPlant, ResourceLocation> IDS = Maps.newIdentityHashMap();

    private static final IIngredientHelper<IAgriPlant> HELPER = new IIngredientHelper<>() {
        @Override
        public IIngredientType<IAgriPlant> getIngredientType() {
            return TYPE;
        }

        @Override
        public String getDisplayName(IAgriPlant plant) {
            return plant.getPlantName().getString();
        }

        @Override
        public String getUniqueId(IAgriPlant plant, UidContext context) {
            return plant.getId();
        }

        @Override
        @Deprecated
        @SuppressWarnings("deprecation")
        public String getModId(IAgriPlant plant) {
            return this.getResourceLocation(plant).getNamespace();
        }

        @Override
        @Deprecated
        @SuppressWarnings("deprecation")
        public String getResourceId(IAgriPlant plant) {
            return this.getResourceLocation(plant).getPath();
        }

        @Override
        public  ResourceLocation getResourceLocation(IAgriPlant plant) {
            return IDS.computeIfAbsent(plant, p -> new ResourceLocation(p.getId().toLowerCase()));
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

    public static class Renderer implements IAgriIngredientRenderer<IAgriPlant>, IAgriGrowableGuiRenderer.RenderContext {
        private final Map<IAgriPlant, IAgriGrowthStage> stageMap = new IdentityHashMap<>();

        public void useGrowthStageForNextRenderCall(IAgriPlant plant, IAgriGrowthStage stage) {
            // It is an ugly hack, but JEI does not really provide anything to handle this elegantly
            stageMap.put(plant, stage);
        }

        @Override
        public void render(@Nonnull PoseStack transform, @Nullable IAgriPlant plant) {
            if(plant == null) {
                return;
            }
            Optional.ofNullable(stageMap.remove(plant)).map(stage -> {
                    this.renderStage(transform, plant, stage);
                    return true;
            }).orElseGet(() ->  {
                plant.getGrowthStages().stream().filter(IAgriGrowthStage::isMature).findFirst().ifPresent(stage ->
                    this.renderStage(transform, plant, stage));
                return false;
            });
        }

        private void renderStage(@Nonnull PoseStack transform, IAgriPlant plant, IAgriGrowthStage stage) {
            this.bindTextureAtlas();
            plant.getGuiRenderer().drawGrowthStage(plant, stage, this, transform, 0, 0, 16, 16);
        }

        @Nonnull
        @Override
        public List<Component> getTooltip(IAgriPlant plant, @Nonnull TooltipFlag iTooltipFlag) {
            List<Component> tooltip = Lists.newArrayList();
            plant.addTooltip(tooltip::add);
            return tooltip;
        }

        @Override
        public void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float width, float height, float r, float g, float b, float a) {
            this.bindTextureAtlas();
            RenderSystem.setShaderColor(r, g, b, a);
            Screen.blit(transforms, (int) x, (int) y, 0, (int) width, (int) height, texture);
        }

        @Override
        public TextureAtlasSprite getSprite(ResourceLocation location) {
            return IAgriIngredientRenderer.super.getSprite(location);
        }
    }
}
