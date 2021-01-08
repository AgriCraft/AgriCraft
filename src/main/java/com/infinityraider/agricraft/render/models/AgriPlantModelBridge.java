package com.infinityraider.agricraft.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.infinitylib.render.IRenderUtilities;
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
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class AgriPlantModelBridge implements IBakedModel, IRenderUtilities, Function<RenderMaterial, TextureAtlasSprite> {
    private static final ModelProperty<IAgriPlant> PROPERTY_PLANT = new ModelProperty<>();
    private static final ModelProperty<IAgriGrowthStage> PROPERTY_PLANT_GROWTH = new ModelProperty<>();
    private static final ModelProperty<IAgriWeed> PROPERTY_WEED = new ModelProperty<>();
    private static final ModelProperty<IAgriGrowthStage> PROPERTY_WEED_GROWTH = new ModelProperty<>();

    private final IModelConfiguration config;
    private final QuadTransformer transformer;
    private final ItemOverrideList overrides;
    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;

    private final Map<IAgriPlant, Map<IAgriGrowthStage, List<BakedQuad>>> plantQuads;
    private final Map<IAgriWeed, Map<IAgriGrowthStage, List<BakedQuad>>> weedQuads;

    protected AgriPlantModelBridge(IModelConfiguration config, ItemOverrideList overrides, Function<RenderMaterial, TextureAtlasSprite> spriteGetter) {
        this.config = config;
        this.transformer = new QuadTransformer(this.getModelConfig().getCombinedTransform().getRotation());
        this.overrides = overrides;
        this.spriteGetter = spriteGetter;
        this.plantQuads = Maps.newConcurrentMap();
        this.weedQuads = Maps.newConcurrentMap();
    }

    protected List<BakedQuad> getOrBakeQuads(IAgriPlant plant, IAgriGrowthStage stage) {
        return this.plantQuads
                .computeIfAbsent(plant, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> this.getTransformer().processMany(plant.bakeQuads(aStage)));
    }

    protected List<BakedQuad> getOrBakeQuads(IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weedQuads
                .computeIfAbsent(weed, (aPlant) -> Maps.newConcurrentMap())
                .computeIfAbsent(stage, (aStage) -> this.getTransformer().processMany(weed.bakeQuads(aStage)));
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
        if(data.hasProperty(PROPERTY_PLANT) && data.hasProperty(PROPERTY_PLANT_GROWTH)) {
            quads.addAll(this.getOrBakeQuads(data.getData(PROPERTY_PLANT), data.getData(PROPERTY_PLANT_GROWTH)));
        }
        if(data.hasProperty(PROPERTY_WEED) && data.hasProperty(PROPERTY_WEED_GROWTH)) {
            quads.addAll(this.getOrBakeQuads(data.getData(PROPERTY_WEED), data.getData(PROPERTY_WEED_GROWTH)));
        }
        return quads.build();
    }

    @Override
    public @Nonnull IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData data) {
        // Fetch plant and weed data from the tile
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) tile;
            data.setData(PROPERTY_PLANT, crop.getPlant());
            data.setData(PROPERTY_PLANT_GROWTH, crop.getGrowthStage());
            data.setData(PROPERTY_WEED, crop.getWeeds());
            data.setData(PROPERTY_WEED_GROWTH, crop.getWeedGrowthStage());
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
