package com.infinityraider.agricraft.api.v1.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriRenderable;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Event fired by AgriCraft's default IAgriPlantQuadGenerator when the quads for a plant (or weed) and growth stage are baked.
 * Can be used to add, remove, or modify quads.
 */
@OnlyIn(Dist.CLIENT)
public class PlantQuadBakeEvent extends Event {
    private final IAgriPlantQuadGenerator quadGenerator;
    private final IAgriRenderable plant;
    private final IAgriGrowthStage stage;
    private final Direction direction;
    private final ResourceLocation texture;
    private final TextureAtlasSprite sprite;
    private final AgriPlantRenderType renderType;
    private final int yOffset;

    private final ImmutableList<BakedQuad> originalQuads;
    private final List<BakedQuad> quads;

    public PlantQuadBakeEvent(IAgriPlantQuadGenerator quadGenerator, IAgriRenderable plant, IAgriGrowthStage stage,
                              @Nullable Direction direction, ResourceLocation texture, TextureAtlasSprite sprite,
                              AgriPlantRenderType renderType, int yOffset, List<BakedQuad> quads) {
        this.quadGenerator = quadGenerator;
        this.plant = plant;
        this.stage = stage;
        this.direction = direction;
        this.texture = texture;
        this.sprite = sprite;
        this.renderType = renderType;
        this.yOffset = yOffset;
        this.originalQuads = ImmutableList.copyOf(quads);
        this.quads = Lists.newArrayList(quads);
    }

    /**
     * @return the default AgriCraft quad generator
     */
    public IAgriPlantQuadGenerator getQuadGenerator() {
        return this.quadGenerator;
    }

    /**
     * @return the plant (or weed) for which quads are being baked
     */
    public IAgriRenderable getPlant() {
        return this.plant;
    }

    /**
     * @return the growth stage for which quads are being baked
     */
    public IAgriGrowthStage getGrowthStage() {
        return this.stage;
    }

    /**
     * @return the cull face for which quads are being baked
     */
    @Nullable
    public Direction getCullFace() {
        return this.direction;
    }

    /**
     * @return the texture for which the quads are being baked
     */
    public ResourceLocation getTexture() {
        return this.texture;
    }

    /**
     * @return the sprite for which the quads are being baked (corresponds to the texture, but on the texture atlas)
     */
    public TextureAtlasSprite getSprite() {
        return this.sprite;
    }

    /**
     * @return the render type for which quads are being baked
     */
    public AgriPlantRenderType getRenderType() {
        return this.renderType;
    }

    /**
     * @return The y-offset which has been applied to the quads
     */
    public int getYOffset() {
        return this.yOffset;
    }

    /**
     * @return An immutable list of the original, default quads
     */
    public ImmutableList<BakedQuad> getOriginalQuads() {
        return this.originalQuads;
    }

    /**
     * @return The list containing the modified quads
     */
    public List<BakedQuad> getOutputQuads() {
        return this.quads;
    }

    /**
     * Adds additional quads for the plant model
     * @return this
     */
    public PlantQuadBakeEvent injectQuads(BakedQuad... quads) {
        return this.injectQuads(Arrays.asList(quads));
    }

    /**
     * Adds additional quads for the plant model
     * @return this
     */
    public PlantQuadBakeEvent injectQuads(Collection<BakedQuad> quads) {
        this.getOutputQuads().addAll(quads);
        return this;
    }

    /**
     * Removes quads from the plant model
     * @return this
     */
    public PlantQuadBakeEvent removeQuads(BakedQuad... quads) {
        return this.removeQuads(Arrays.asList(quads));
    }

    /**
     * Removes quads from the plant model
     * @return this
     */
    public PlantQuadBakeEvent removeQuads(Collection<BakedQuad> quads) {
        this.getOutputQuads().removeAll(quads);
        return this;
    }

    /**
     * Removes all quads from the plant model
     * @return this
     */
    public PlantQuadBakeEvent clearQuads() {
        this.getOutputQuads().clear();
        return this;
    }
}
