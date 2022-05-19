package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriSeedModelLoader implements InfModelLoader<AgriSeedModelLoader.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "seed_model_loader");
    private static final ModelOverride MODEL_OVERRIDE = new ModelOverride();

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
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {}

    @Override
    @Nonnull
    public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return this.getGeometry();
    }

    public static class Geometry implements IModelGeometry<AgriSeedModelLoader.Geometry>, IRenderUtilities {
        private Geometry() {}

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState transforms, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new BakedSeedModel(owner);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            ImmutableList.Builder<Material> builder = new ImmutableList.Builder<>();
            AgriPlantRegistry.getInstance().all().stream()
                    .map(IAgriPlant::getSeedTexture)
                    .forEach(rl -> builder.add(new Material(this.getTextureAtlasLocation(), rl)));
            return builder.build();
        }
    }

    public static class ModelOverride extends ItemOverrides implements IRenderUtilities{
        protected ModelOverride() {
            super();
        }

        @Nullable
        @Override
        public BakedModel resolve(@Nonnull BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            if(stack.getItem() instanceof ItemDynamicAgriSeed) {
                BakedModel seedModel =  this.getModelManager().getModel(((ItemDynamicAgriSeed) stack.getItem()).getPlant(stack).getSeedModel());
                return seedModel.getOverrides().resolve(seedModel, stack, world, entity, seed);
            }
            return this.getModelManager().getMissingModel();
        }
    }

    public static class BakedSeedModel implements BakedModel {
        private final IModelConfiguration owner;

        private BakedSeedModel(IModelConfiguration owner) {
            this.owner = owner;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
            return ImmutableList.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return true;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Nonnull
        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.getOverrides().getMissingSprite();
        }

        @Nonnull
        @Override
        @Deprecated
        @SuppressWarnings("deprecation")
        public ItemTransforms getTransforms() {
            return this.owner.getCameraTransforms();
        }

        @Nonnull
        @Override
        public ModelOverride getOverrides() {
            return MODEL_OVERRIDE;
        }
    }
}
