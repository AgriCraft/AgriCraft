package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
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
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriSeedBagSeedModelLoader implements InfModelLoader<AgriSeedBagSeedModelLoader.Geometry> {
    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "seed_bag_loader");

    private static final AgriSeedBagSeedModelLoader INSTANCE = new AgriSeedBagSeedModelLoader();

    public static AgriSeedBagSeedModelLoader getInstance() {
        return INSTANCE;
    }

    private final Geometry geometry;

    private AgriSeedBagSeedModelLoader() {
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
        private Geometry() {
        }

        @Override
        public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                IModelTransform transforms, ItemOverrideList overrides, ResourceLocation modelLocation) {
            return new BakedModel(owner, overrides);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            ImmutableList.Builder<RenderMaterial> builder = new ImmutableList.Builder<>();
            AgriPlantRegistry.getInstance().all().stream()
                    .map(IAgriPlant::getSeedTexture)
                    .forEach(rl -> builder.add(new RenderMaterial(this.getTextureAtlasLocation(), rl)));
            return builder.build();
        }
    }

    public static class BakedModel implements IBakedModel, IRenderUtilities {
        private IBakedModel baseModel;
        private final Map<IAgriPlant, IBakedModel> seedQuads;
        private final Map<Direction, List<BakedQuad>> emptyMap;
        private final IModelConfiguration owner;
        private final ItemOverrideList overrides;

        private BakedModel(IModelConfiguration owner, ItemOverrideList overrides) {
            this.seedQuads = Maps.newIdentityHashMap();
            this.emptyMap = this.generateEmptyQuadMap();
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
            // Check if the stack is a bag
            if(!(stack.getItem() instanceof ItemSeedBag)) {
                return Collections.emptyList();
            }
            // Check if the bag is activated
            ItemSeedBag bag = (ItemSeedBag) stack.getItem();
            if(!bag.isActivated(stack)) {
                return Collections.singletonList(Pair.of(this.getBaseModel(), RenderTypeLookup.func_239219_a_(stack, fabulous)));
            }
            // Check if the bag contains seeds
            ItemSeedBag.IContents contents = bag.getContents(stack);
            if(!contents.getPlant().isPlant()) {
                return Collections.singletonList(Pair.of(this.getBaseModel(), RenderTypeLookup.func_239219_a_(stack, fabulous)));
            }
            // Fetch the quads
            RenderType type = RenderTypeLookup.func_239219_a_(stack, fabulous);
            return Lists.newArrayList(
                    Pair.of(this.getBaseModel(), type),
                    Pair.of(this.getOrBakeSeedModel(contents.getPlant()), type)
            );
        }

        protected IBakedModel getBaseModel() {
            if(this.baseModel == null) {
                this.baseModel = this.getModelManager().getModel(
                        new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag"));
            }
            return this.baseModel;
        }

        protected IBakedModel getOrBakeSeedModel(IAgriPlant plant) {
            return this.seedQuads.computeIfAbsent(plant, this::bakeSeedModel);
        }

        protected IBakedModel bakeSeedModel(IAgriPlant plant) {
            ResourceLocation texture = plant.getSeedTexture();
            TextureAtlasSprite sprite = this.getSprite(texture);
            return new SimpleBakedModel(
                    this.bakeQuads(sprite), this.emptyMap,
                    false, true, false, sprite,
                    ItemCameraTransforms.DEFAULT, ItemOverrideList.EMPTY);
        }

        protected List<BakedQuad> bakeQuads(TextureAtlasSprite sprite) {
            ITessellator tessellator = this.getBakedQuadTessellator();
            tessellator.startDrawingQuads();
            tessellator.setFace(ITessellator.Face.GENERAL);
            tessellator.drawScaledFace(4, 2, 12, 10, Direction.NORTH, sprite, 7.499F,
                    0, 0, 16, 16);
            tessellator.drawScaledFace(4, 2, 12, 10, Direction.SOUTH, sprite, 8.501F,
                    0, 0, 16, 16);
            List<BakedQuad> quads = tessellator.getQuads();
            tessellator.draw();
            return quads;
        }

        private Map<Direction, List<BakedQuad>> generateEmptyQuadMap() {
            Map<Direction, List<BakedQuad>> map = Maps.newEnumMap(Direction.class);
            List<BakedQuad> quads = Collections.emptyList();
            Arrays.stream(Direction.values()).forEach(dir -> map.put(dir, quads));
            return ImmutableMap.copyOf(map);
        }
    }
}
