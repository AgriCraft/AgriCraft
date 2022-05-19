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
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
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
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {}

    @Override
    @Nonnull
    public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return this.getGeometry();
    }

    public static class Geometry implements IModelGeometry<Geometry>, IRenderUtilities {
        private Geometry() {
        }

        @Override
        public BakedBagModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState transforms, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new BakedBagModel(owner, overrides);
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

    public static class BakedBagModel implements BakedModel, IRenderUtilities {
        private final Map<IAgriPlant, BakedModel> seedQuads;
        private final Map<Direction, List<BakedQuad>> emptyMap;
        private final IModelConfiguration owner;
        private final ItemOverrides overrides;

        private BakedModel baseModelEmpty;
        private BakedModel baseModelPartial;
        private BakedModel baseModelFull;

        private BakedBagModel(IModelConfiguration owner, ItemOverrides overrides) {
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
            return this.getMissingSprite();
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
        public ItemOverrides getOverrides() {
            return this.overrides;
        }

        @Override
        public boolean isLayered() {
            return true;
        }

        @Override
        public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack stack, boolean fabulous) {
            // Check if the stack is a bag
            if(!(stack.getItem() instanceof ItemSeedBag)) {
                return Collections.emptyList();
            }
            // Check if the bag is activated
            ItemSeedBag bag = (ItemSeedBag) stack.getItem();
            if(!bag.isActivated(stack)) {
                return Collections.singletonList(Pair.of(this.getEmptyBagModel(), ItemBlockRenderTypes.getRenderType(stack, fabulous)));
            }
            // Check if the bag contains seeds
            ItemSeedBag.Contents contents = bag.getContents(stack);
            if(!contents.getPlant().isPlant()) {
                return Collections.singletonList(Pair.of(this.getEmptyBagModel(), ItemBlockRenderTypes.getRenderType(stack, fabulous)));
            }
            // Fetch the quads
            RenderType type = ItemBlockRenderTypes.getRenderType(stack, fabulous);
            return Lists.newArrayList(
                    Pair.of(contents.isFull() ? this.getFullBagModel() : this.getPartialBagModel(), type),
                    Pair.of(this.getOrBakeSeedModel(contents.getPlant()), type)
            );
        }

        protected BakedModel getEmptyBagModel() {
            if(this.baseModelEmpty == null) {
                this.baseModelEmpty = this.getModelManager().getModel(
                        new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_empty"));
            }
            return this.baseModelEmpty;
        }

        protected BakedModel getPartialBagModel() {
            if(this.baseModelPartial == null) {
                this.baseModelPartial = this.getModelManager().getModel(
                        new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_partial"));
            }
            return this.baseModelPartial;
        }

        protected BakedModel getFullBagModel() {
            if(this.baseModelFull == null) {
                this.baseModelFull = this.getModelManager().getModel(
                        new ResourceLocation(AgriCraft.instance.getModId(), "item/agri_seed_bag_full"));
            }
            return this.baseModelFull;
        }

        protected BakedModel getOrBakeSeedModel(IAgriPlant plant) {
            return this.seedQuads.computeIfAbsent(plant, this::bakeSeedModel);
        }

        protected BakedModel bakeSeedModel(IAgriPlant plant) {
            ResourceLocation texture = plant.getSeedTexture();
            TextureAtlasSprite sprite = this.getSprite(texture);
            return new SimpleBakedModel(
                    this.bakeQuads(sprite), this.emptyMap,
                    false, true, false, sprite,
                    ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY);
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
