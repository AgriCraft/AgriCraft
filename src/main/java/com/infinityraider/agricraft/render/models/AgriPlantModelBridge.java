package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.content.core.TileEntityCropBase;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.infinityraider.infinitylib.render.QuadCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
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
public class AgriPlantModelBridge implements IBakedModel, IRenderUtilities, Function<RenderMaterial, TextureAtlasSprite> {
    private final IModelConfiguration config;
    private final QuadTransformer transformer;
    private final ItemOverrideList overrides;
    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;

    private static final Map<IAgriPlant, Map<IAgriGrowthStage, QuadCache>> plantQuads = Maps.newConcurrentMap();
    private static final Map<IAgriWeed, Map<IAgriGrowthStage, QuadCache>> weedQuads = Maps.newConcurrentMap();

    protected AgriPlantModelBridge(IModelConfiguration config, ItemOverrideList overrides, Function<RenderMaterial, TextureAtlasSprite> spriteGetter) {
        this.config = config;
        this.transformer = new QuadTransformer(this.getModelConfig().getCombinedTransform().getRotation());
        this.overrides = overrides;
        this.spriteGetter = spriteGetter;
    }

    protected static List<BakedQuad> getOrBakeQuads(IAgriPlant plant, IAgriGrowthStage stage, Direction face) {
        return plantQuads
                .computeIfAbsent(plant, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> new QuadCache(dir -> plant.bakeQuads(dir, stage)))
                .getQuads(face);
    }

    protected static List<BakedQuad> getOrBakeQuads(IAgriWeed weed, IAgriGrowthStage stage, Direction face) {
        return weedQuads
                .computeIfAbsent(weed, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> new QuadCache(dir -> weed.bakeQuads(dir, stage)))
                .getQuads(face);
    }

    protected IModelConfiguration getModelConfig() {
        return this.config;
    }

    protected QuadTransformer getTransformer() {
        return this.transformer;
    }

    @Override
    public TextureAtlasSprite apply(RenderMaterial renderMaterial) {
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
        if(data.hasProperty(TileEntityCropBase.PROPERTY_PLANT) && data.hasProperty(TileEntityCropBase.PROPERTY_PLANT_GROWTH)) {
            quads.addAll(getOrBakeQuads(data.getData(TileEntityCropBase.PROPERTY_PLANT), data.getData(TileEntityCropBase.PROPERTY_PLANT_GROWTH), side));
        }
        if(data.hasProperty(TileEntityCropBase.PROPERTY_WEED) && data.hasProperty(TileEntityCropBase.PROPERTY_WEED_GROWTH)) {
            quads.addAll(getOrBakeQuads(data.getData(TileEntityCropBase.PROPERTY_WEED), data.getData(TileEntityCropBase.PROPERTY_WEED_GROWTH), side));
        }
        return quads.build();
    }

    @Override
    public @Nonnull IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        // Fetch plant and weed data from the tile
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) tile;
            data.setData(TileEntityCropBase.PROPERTY_PLANT, crop.getPlant());
            data.setData(TileEntityCropBase.PROPERTY_PLANT_GROWTH, crop.getGrowthStage());
            data.setData(TileEntityCropBase.PROPERTY_WEED, crop.getWeeds());
            data.setData(TileEntityCropBase.PROPERTY_WEED_GROWTH, crop.getWeedGrowthStage());
        }
        return data;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        //TODO
        return this.getMissingSprite();
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }
}
