package com.infinityraider.agricraft.api.v1.client;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Enum representing the different types for which AgriCraft plants / weeds are rendered
 *
 * Create new instances using the static create() method.
 * New instances will also work with AgriCraft's json system
 */
@OnlyIn(Dist.CLIENT)
public enum AgriPlantRenderType implements IExtensibleEnum {
    /** Renders in a hashtag pattern (#); 4 faces parallel with the block faces, similar to Vanilla wheat */
    HASH(Identifiers.hash(), (plant, stage, dir, sprite) -> {
        return plantQuadGenerator().bakeQuadsForHashPattern(plant, stage, dir, sprite);
    }),

    /** Renders in a cross pattern (x); 2 faces along the diagonals, similar to Vanilla flowers */
    CROSS(Identifiers.cross(), (plant, stage, dir, sprite) -> {
        return plantQuadGenerator().bakeQuadsForCrossPattern(plant, stage, dir, sprite);
    }),

    /** Renders in a plus pattern (+); similar to cross, but instead 4 crosses at each crop stick */
    PLUS(Identifiers.plus(), (plant, stage, dir, sprite) -> {
        return plantQuadGenerator().bakeQuadsForPlusPattern(plant, stage, dir, sprite);
    }),

    /** Renders in a rhombus pattern (◇); 4 faces spanning between the centers of the block faces, only used for weeds */
    RHOMBUS(Identifiers.rhombus(), (plant, stage, dir, sprite) -> {
        return plantQuadGenerator().bakeQuadsForRhombusPattern(plant, stage, dir, sprite);
    }),

    /** Renders for a gourd pattern (@); i.e. for pumpkins and melons: renders a hash pattern for the initial stages, with a small gourd for the final stage */
    GOURD(Identifiers.gourd(), (plant, stage, dir, sprite) -> {
        return plantQuadGenerator().bakeQuadsForGourdPattern(plant, stage, dir, sprite);
    });

    private final Predicate<String> nameFilter;
    private final IQuadGenerator generator;

    AgriPlantRenderType(Predicate<String> nameFilter, IQuadGenerator generator) {
        this.nameFilter = nameFilter;
        this.generator = generator;
    }

    /**
     * Checks if an identifier matches this enum value
     * @param id the identifier
     * @return true if the identifier describes this render type
     */
    public boolean matches(String id) {
        return this.nameFilter.test(id);
    }

    /**
     * Generates the quads for the render type
     * @param plant the plant (can be cast to either to IAgriPlant or IAgriWeed)
     * @param stage the growth stage
     * @param direction the cull-face
     * @param spriteFunc function to obtain a sprite per vertical layer
     * @return list of BakedQuads
     */
    @Nonnull
    public List<BakedQuad> bakedQuads(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction direction,
                                      IntFunction<TextureAtlasSprite> spriteFunc) {
        return this.generator.bakeQuads(plant, stage, direction, spriteFunc);
    }

    /**
     * Static method to create a custom render type.
     *
     * @param name the name (i.e. equivalent to "HASH", "CROSS", "PLUS", or "RHOMBUS"
     * @param nameFilter predicate for json identifiers of this render type
     * @param generator the quad generator
     * @return a new AgriPlantRenderType instance
     */
    @SuppressWarnings("unused")
    public static AgriPlantRenderType create(String name, Predicate<String> nameFilter, IQuadGenerator generator) {
        throw new IllegalStateException("Enum not extended");
    }

    /**
     * Static method to fetch render type from an identifier.
     *
     * @param id the identifier
     * @return optional holding the render type for the identifier, or empty if none matches
     */
    public static Optional<AgriPlantRenderType> fetchFromIdentifier(String id) {
        return Arrays.stream(values()).filter(type -> type.matches(id)).findAny();
    }

    /**
     * Static utility method to fetch the default AgriCraft IAgriPlantQuadGenerator instance
     *
     * @return the default AgriCraft IAgriPlantQuadGenerator instance
     */
    public static IAgriPlantQuadGenerator plantQuadGenerator() {
        return IAgriPlantQuadGenerator.getInstance();
    }

    @FunctionalInterface
    public interface IQuadGenerator {
        /**
         * Method which will generate quads for a plant.
         *
         * The yOffset parameter is used for plants which are taller than 1 block.         *
         * For taller plants this method will be called multiple times, for instance for a plant that is three blocks tall,
         * this method will be called three times: once for the base layer (yOffset = 0), again for the middle layer
         * (yOffset = 1), and a final time for the top layer (yOffset = 2).
         *
         * @param plant the plant (can be cast to either to IAgriPlant or IAgriWeed)
         * @param stage the growth stage
         * @param direction the cull-face
         * @param spriteFunc function returning sprites per vertical layer
         * @return list of BakedQuads
         */
        @Nonnull
        List<BakedQuad> bakeQuads(IAgriGrowable plant, IAgriGrowthStage stage, @Nullable Direction direction, IntFunction<TextureAtlasSprite> spriteFunc);
    }

    /**
     * Wrapper class holding reference identifier strings
     */
    public static final class Identifiers {
        public static final List<String> HASH = ImmutableList.of("hash", "#");
        public static final List<String> CROSS = ImmutableList.of("cross", "X");
        public static final List<String> PLUS = ImmutableList.of("plus", "+");
        public static final List<String> RHOMBUS = ImmutableList.of("rhombus", "⬦", "◇");
        public static final List<String> GOURD = ImmutableList.of("gourd", "@");

        public static Predicate<String> predicate(List<String> entries) {
            return (string) -> entries.stream().anyMatch(entry -> entry.equalsIgnoreCase(string));
        }

        public static Predicate<String> hash() {
            return predicate(HASH);
        }

        public static Predicate<String> cross() {
            return predicate(CROSS);
        }

        public static Predicate<String> plus() {
            return predicate(PLUS);
        }

        public static Predicate<String> rhombus() {
            return predicate(RHOMBUS);
        }

        public static Predicate<String> gourd() {
            return predicate(GOURD);
        }

        private Identifiers() throws IllegalAccessException {
            throw new IllegalAccessException("Whomst dareth init?");
        }
    }
}
