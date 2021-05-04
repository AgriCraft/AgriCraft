package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriRenderable;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriWeedRegistry;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriPlantModelLoader implements InfModelLoader<AgriPlantModelLoader.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "plant_model_loader");

    private static final AgriPlantModelLoader INSTANCE = new AgriPlantModelLoader();

    public static AgriPlantModelLoader getInstance() {
        return INSTANCE;
    }

    private final Geometry geometry;

    private AgriPlantModelLoader() {
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
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {}

    @Override
    @Nonnull
    public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return this.getGeometry();
    }

    public static class Geometry implements IModelGeometry<Geometry>, IRenderUtilities {
        private Geometry() {}

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
                return new AgriPlantModelBridge(owner, overrides, spriteGetter);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            ImmutableList.Builder<RenderMaterial> builder = new ImmutableList.Builder<>();
            this.processRegistryTextures(AgriPlantRegistry.getInstance(), rl -> builder.add(new RenderMaterial(this.getTextureAtlasLocation(), rl)));
            this.processRegistryTextures(AgriWeedRegistry.getInstance(), rl -> builder.add(new RenderMaterial(this.getTextureAtlasLocation(), rl)));
            return builder.build();
        }

        protected <T extends IAgriRegisterable<T> & IAgriGrowable & IAgriRenderable> void processRegistryTextures(IAgriRegistry<T> registry, Consumer<ResourceLocation> consumer) {
            registry.all().stream()
                    .flatMap((element) -> element.getGrowthStages()
                            .stream().flatMap(stage -> element.getTexturesFor(stage).stream()))
                    .forEach(consumer);
        }
    }
}
