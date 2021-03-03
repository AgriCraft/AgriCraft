package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriRenderable;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlant;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriSeedModelLoader implements InfModelLoader<AgriSeedModelLoader.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "seed_model_loader");

    private static final AgriSeedModelLoader INSTANCE = new AgriSeedModelLoader();

    public static AgriSeedModelLoader getInstance() {
        return INSTANCE;
    }

    private final Geometry geometry;

    private AgriSeedModelLoader() {
        this.geometry = new Geometry();
    }

    protected Geometry getGeometry() {
        return this.geometry;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        //TODO
    }

    @Override
    @Nonnull
    public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return this.getGeometry();
    }

    public static class Geometry implements IModelGeometry<AgriSeedModelLoader.Geometry>, IRenderUtilities {
        private Geometry() {}

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                IModelTransform transforms, ItemOverrideList overrides, ResourceLocation modelLocation) {
            return new BakedModel(owner, overrides);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            ImmutableList.Builder<RenderMaterial> builder = new ImmutableList.Builder<>();
            this.processRegistryTextures(AgriPlantRegistry.getInstance(), rl -> builder.add(new RenderMaterial(this.getTextureAtlasLocation(), rl)));
            return builder.build();
        }

        protected <T extends IAgriRegisterable<T> & IAgriGrowable & IAgriRenderable> void processRegistryTextures(IAgriRegistry<T> registry, Consumer<ResourceLocation> consumer) {
            registry.stream()
                    .filter(element -> element instanceof JsonPlant)
                    .map(element -> (JsonPlant) element)
                    .map(JsonPlant::getSeedModel)
                    .forEach(consumer);
        }
    }

    public static class BakedModel implements IBakedModel, IRenderUtilities {
        private final IModelConfiguration owner;
        private final ItemOverrideList overrides;

        private BakedModel(IModelConfiguration owner, ItemOverrideList overrides) {
            this.owner = owner;
            this.overrides = overrides;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
            return ImmutableList.of();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean isSideLit() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleTexture() {
            return this.getMissingSprite();
        }

        @Nonnull
        @Override
        @Deprecated
        @SuppressWarnings("deprecation")
        public ItemCameraTransforms getItemCameraTransforms() {
            return this.owner.getCameraTransforms();
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return this.overrides;
        }

        @Override
        public boolean isLayered() {
            return true;
        }

        @Override
        public List<Pair<IBakedModel, RenderType>> getLayerModels(ItemStack stack, boolean fabulous) {
            return Collections.singletonList(Pair.of(
                    this.getModelManager().getModel(((ItemDynamicAgriSeed) stack.getItem()).getPlant(stack).getSeedModel()),
                    RenderTypeLookup.func_239219_a_(stack, fabulous)));
        }
    }
}
