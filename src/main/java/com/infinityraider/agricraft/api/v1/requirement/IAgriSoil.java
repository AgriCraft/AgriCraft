package com.infinityraider.agricraft.api.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.util.IAgriDisplayable;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Class for interacting with AgriCraft soil definitions.
 *
 * Retrieve instances from the World using AgriApi.getSoil(world, pos)
 * Retrieve instances from a Block state using ISoilRegistry.valueOf(state)
 */
public interface IAgriSoil extends IAgriRegisterable<IAgriSoil>, IAgriDisplayable {

    @Nonnull
    @Override
    String getId();

    @Nonnull
    ITextComponent getName();

    @Nonnull
    Collection<BlockState> getVariants();

    @Nonnull
    Humidity getHumidity();

    @Nonnull
    Acidity getAcidity();

    @Nonnull
    Nutrients getNutrients();

    double getGrowthModifier();

    boolean isSoil();

    default boolean isVariant(@Nonnull BlockState state) {
        return this.getVariants().contains(state);
    }

    @Override
    default void addDisplayInfo(@Nonnull Consumer<ITextComponent> consumer) {
        consumer.accept(Tooltips.SOIL);
        consumer.accept(Tooltips.tooltipHumidity(this));
        consumer.accept(Tooltips.tooltipAcidity(this));
        consumer.accept(Tooltips.tooltipNutrients(this));
    }

    /**
     * Enum for describing the humidity property of soils
     */
    enum Humidity implements SoilProperty {
        ARID,
        DRY,
        DAMP("moist"),
        WET("standard", "default"),
        WATERY,
        FLOODED,
        INVALID;

        private final ITextComponent textComponent;
        private final List<String> synonyms;

        Humidity(String... synonyms) {
            this.textComponent = new TranslationTextComponent("agricraft.tooltip.humidity." + this.name().toLowerCase());
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
        }

        @Override
        public ITextComponent getDescription() {
            return this.textComponent;
        }

        @Override
        public boolean isValid() {
            return this != INVALID;
        }

        @Override
        public List<String> getSynonyms() {
            return this.synonyms;
        }

        public static Optional<Humidity> fromString(final String string) {
            return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
        }
    }

    /**
     * Enum for describing the acidity property of soils
     */
    enum Acidity implements SoilProperty {
        HIGHLY_ACIDIC( "0", "1", "2", "highly-acidic", "highly acidic", "very-acidic", "very acidic", "very_acidic"),
        ACIDIC("3", "4", "5"),
        SLIGHTLY_ACIDIC("6", "slightly-acidic", "slightly acidic", "standard", "default"),
        NEUTRAL("7"),
        SLIGHTLY_ALKALINE("8", "slightly-alkaline", "slightly alkaline"),
        ALKALINE("9", "10", "11"),
        HIGHLY_ALKALINE("12", "13", "14", "highly-alkaline", "highly alkaline", "very-alkaline", "very alkaline", "very_alkaline"),
        INVALID;

        private final ITextComponent textComponent;
        private final List<String> synonyms;

        Acidity(String... synonyms) {
            this.textComponent = new TranslationTextComponent("agricraft.tooltip.acidity." + this.name().toLowerCase());
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
        }

        @Override
        public ITextComponent getDescription() {
            return this.textComponent;
        }

        @Override
        public boolean isValid() {
            return this != INVALID;
        }

        @Override
        public List<String> getSynonyms() {
            return this.synonyms;
        }

        public static Optional<Acidity> fromString(final String string) {
            return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
        }
    }

    /**
     * Enum for describing the nutrients property of soils
     */
    enum Nutrients implements SoilProperty {
        NONE("zero", "empty"),
        VERY_LOW("scarce", "poor"),
        LOW,
        MEDIUM("normal", "average"),
        HIGH("standard", "default"),
        VERY_HIGH("rich"),
        INVALID;

        private final ITextComponent textComponent;
        private final List<String> synonyms;

        Nutrients(String... synonyms) {
            this.textComponent = new TranslationTextComponent("agricraft.tooltip.nutrients." + this.name().toLowerCase());
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
        }

        @Override
        public ITextComponent getDescription() {
            return this.textComponent;
        }

        @Override
        public boolean isValid() {
            return this != INVALID;
        }

        @Override
        public List<String> getSynonyms() {
            return this.synonyms;
        }

        public static Optional<Nutrients> fromString(final String string) {
            return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
        }
    }

    interface SoilProperty {
        ITextComponent getDescription();

        List<String> getSynonyms();

        boolean isValid();

        default boolean isSynonym(String string) {
            return this.getSynonyms().stream().anyMatch(string::equalsIgnoreCase);
        }

        String name();

        int ordinal();
    }

    final class Tooltips {
        private Tooltips() {}

        public static final ITextComponent SOIL = new TranslationTextComponent("agricraft.tooltip.soil").mergeStyle(TextFormatting.DARK_GRAY);

        public static final ITextComponent HUMIDITY = new TranslationTextComponent("agricraft.tooltip.humidity").mergeStyle(TextFormatting.DARK_GRAY);
        public static final ITextComponent ACIDITY = new TranslationTextComponent("agricraft.tooltip.acidity").mergeStyle(TextFormatting.DARK_GRAY);
        public static final ITextComponent NUTRIENTS = new TranslationTextComponent("agricraft.tooltip.nutrients").mergeStyle(TextFormatting.DARK_GRAY);

        private static ITextComponent tooltipHumidity(IAgriSoil soil) {
            return new StringTextComponent(" - ")
                    .append(HUMIDITY).append(new StringTextComponent(": "))
                    .append(soil.getHumidity().getDescription())
                    .mergeStyle(TextFormatting.DARK_GRAY);
        }

        private static ITextComponent tooltipAcidity(IAgriSoil soil) {
            return new StringTextComponent(" - ")
                    .append(ACIDITY).append(new StringTextComponent(": "))
                    .append(soil.getAcidity().getDescription())
                    .mergeStyle(TextFormatting.DARK_GRAY);
        }

        private static ITextComponent tooltipNutrients(IAgriSoil soil) {
            return new StringTextComponent(" - ")
                    .append(NUTRIENTS).append(new StringTextComponent(": "))
                    .append(soil.getNutrients().getDescription())
                    .mergeStyle(TextFormatting.DARK_GRAY);
        }
    }
}
