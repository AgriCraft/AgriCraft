package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.content.core.TileEntityCrop;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.QuadCache;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriPlantModelBridge implements BakedModel, IRenderUtilities, Function<Material, TextureAtlasSprite> {
    private final IModelConfiguration config;
    private final QuadTransformer transformer;
    private final ItemOverrides overrides;
    private final Function<Material, TextureAtlasSprite> spriteGetter;

    private static final Map<IAgriPlant, Map<IAgriGrowthStage, QuadCache>> plantQuads = Maps.newConcurrentMap();
    private static final Map<IAgriWeed, Map<IAgriGrowthStage, QuadCache>> weedQuads = Maps.newConcurrentMap();

    protected AgriPlantModelBridge(IModelConfiguration config, ItemOverrides overrides, Function<Material, TextureAtlasSprite> spriteGetter) {
        this.config = config;
        this.transformer = new QuadTransformer(this.getModelConfig().getCombinedTransform().getRotation());
        this.overrides = overrides;
        this.spriteGetter = spriteGetter;
    }

    @SuppressWarnings("unchecked")
    public static List<BakedQuad> getOrBakeQuads(IAgriPlant plant, IAgriGrowthStage stage, @Nullable Direction face) {
        return plantQuads
                .computeIfAbsent(plant, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> new QuadCache(dir -> (List<BakedQuad>) plant.bakeQuads(dir, stage)))
                .getQuads(face);
    }

    @SuppressWarnings("unchecked")
    public static List<BakedQuad> getOrBakeQuads(IAgriWeed weed, IAgriGrowthStage stage, @Nullable Direction face) {
        return weedQuads
                .computeIfAbsent(weed, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> new QuadCache(dir -> (List<BakedQuad>) weed.bakeQuads(dir, stage)))
                .getQuads(face);
    }

    protected IModelConfiguration getModelConfig() {
        return this.config;
    }

    protected QuadTransformer getTransformer() {
        return this.transformer;
    }

    @Override
    public TextureAtlasSprite apply(Material renderMaterial) {
        return this.spriteGetter.apply(renderMaterial);
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        // Without tile data, return an empty list
        return ImmutableList.of();
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        // Fetch quads based on the plant and weed in the data
        ImmutableList.Builder<BakedQuad> quads = new ImmutableList.Builder<>();
        if(data.hasProperty(TileEntityCrop.PROPERTY_PLANT) && data.hasProperty(TileEntityCrop.PROPERTY_PLANT_GROWTH)) {
            quads.addAll(getOrBakeQuads(data.getData(TileEntityCrop.PROPERTY_PLANT), data.getData(TileEntityCrop.PROPERTY_PLANT_GROWTH), side));
        }
        if(data.hasProperty(TileEntityCrop.PROPERTY_WEED) && data.hasProperty(TileEntityCrop.PROPERTY_WEED_GROWTH)) {
            quads.addAll(getOrBakeQuads(data.getData(TileEntityCrop.PROPERTY_WEED), data.getData(TileEntityCrop.PROPERTY_WEED_GROWTH), side));
        }
        return quads.build();
    }

    @Override
    public @Nonnull IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        // Fetch plant and weed data from the tile
        AgriApi.getCrop(world, pos).ifPresent(crop -> {
            data.setData(TileEntityCrop.PROPERTY_PLANT, crop.getPlant());
            data.setData(TileEntityCrop.PROPERTY_PLANT_GROWTH, crop.getGrowthStage());
            data.setData(TileEntityCrop.PROPERTY_WEED, crop.getWeeds());
            data.setData(TileEntityCrop.PROPERTY_WEED_GROWTH, crop.getWeedGrowthStage());
        });
        return data;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
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
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        if(data.hasProperty(TileEntityCrop.PROPERTY_PLANT) && data.hasProperty(TileEntityCrop.PROPERTY_PLANT_GROWTH)) {
            IAgriPlant plant = data.getData(TileEntityCrop.PROPERTY_PLANT);
            if(plant != null) {
                return this.getSprite(plant.getTexturesFor(data.getData(TileEntityCrop.PROPERTY_PLANT_GROWTH)).get(0));
            }
        }
        if(data.hasProperty(TileEntityCrop.PROPERTY_WEED) && data.hasProperty(TileEntityCrop.PROPERTY_WEED_GROWTH)) {
            IAgriWeed weed = data.getData(TileEntityCrop.PROPERTY_WEED);
            if(weed != null) {
                return this.getSprite(weed.getTexturesFor(data.getData(TileEntityCrop.PROPERTY_WEED_GROWTH)).get(0));
            }

        }
        return this.getParticleIcon();
    }

    @Override
    @Nonnull
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
}
