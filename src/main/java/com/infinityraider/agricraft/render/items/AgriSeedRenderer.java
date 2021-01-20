package com.infinityraider.agricraft.render.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriSeedRenderer implements InfItemRenderer, IRenderUtilities {
    private static final AgriSeedRenderer INSTANCE = new AgriSeedRenderer();
    private static final ResourceLocation GENERATED = new ResourceLocation("builtin/generated");

    public static AgriSeedRenderer getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, IBakedModel> models;

    private AgriSeedRenderer() {
        this.models = Maps.newConcurrentMap();
    }

    @Override
    public void render(ItemStack stack, ItemCameraTransforms.TransformType perspective, MatrixStack transforms,
                       IRenderTypeBuffer buffer, int light, int overlay) {
        if(!(stack.getItem() instanceof ItemDynamicAgriSeed)) {
            return;
        }
        ItemDynamicAgriSeed seed = (ItemDynamicAgriSeed) stack.getItem();
        IAgriPlant plant = seed.getPlant(stack);
        ResourceLocation texture = plant.getSeedTexture();
        IBakedModel model = this.getModel(texture, transforms, perspective);
        IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderTypeLookup.func_239219_a_(stack, true), true, stack.hasEffect());
        this.getItemRenderer().renderModel(model, stack, light, overlay, transforms, vertexBuilder);
    }

    private IBakedModel getModel(ResourceLocation texture, MatrixStack transforms, ItemCameraTransforms.TransformType perspective) {
        IBakedModel model = models.getOrDefault(texture, this.getModelManager().getMissingModel());
        return ForgeHooksClient.handleCameraTransforms(transforms, model, perspective, this.isLeftHand(perspective));
    }

    private boolean isLeftHand(ItemCameraTransforms.TransformType perspective) {
        return perspective == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND
                || perspective == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
    }

    public void bakeModels(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transforms, boolean guiLight3d) {
        AgriApi.getPlantRegistry().stream()
                .forEach(plant -> {
                    ResourceLocation texture = plant.getSeedTexture();
                    ResourceLocation modelLocation = new ResourceLocation(plant.getId());
                    Map<String, Either<RenderMaterial, String>> textures = Maps.newHashMap();
                    textures.put("particle", Either.right(texture.toString()));
                    textures.put("layer0", Either.right(texture.toString()));
                    BlockModel blockModel = new BlockModel(
                            GENERATED,
                            ImmutableList.of(),
                            textures,
                            false,
                            BlockModel.GuiLight.FRONT,
                            ItemCameraTransforms.DEFAULT,
                            ImmutableList.of());
                    this.models.put(texture, blockModel.bakeModel(bakery, blockModel, spriteGetter, transforms, modelLocation, guiLight3d));
                });

    }
}
