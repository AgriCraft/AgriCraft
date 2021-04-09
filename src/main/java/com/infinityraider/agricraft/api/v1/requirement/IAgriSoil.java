package com.infinityraider.agricraft.api.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.text.ITextComponent;

/**
 * Class for interacting with AgriCraft soil definitions.
 */
public interface IAgriSoil extends IAgriRegisterable<IAgriSoil> {

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

    default boolean isVariant(@Nonnull BlockState state) {
        return this.getVariants().contains(state);
    }

    enum Humidity implements SynonymEnum<Humidity> {
        ARID,
        DRY,
        DAMP("moist", "default"),
        WET,
        WATERY,
        FLOODED,
        INVALID;

        private final List<String> synonyms;

        Humidity(String... synonyms) {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
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

    enum Acidity implements SynonymEnum<Acidity> {
        HIGHLY_ACIDIC( "0", "1", "2", "highly-acidic", "highly acidic", "very-acidic", "very acidic", "very_acidic"),
        ACIDIC("3", "4", "5"),
        SLIGHTLY_ACIDIC("6", "slightly-acidic", "slightly acidic"),
        NEUTRAL("7", "default", "standard"),
        SLIGHTLY_ALKALINE("8", "slightly-alkaline", "slightly alkaline"),
        ALKALINE("9", "10", "11"),
        HIGHLY_ALKALINE("12", "13", "14", "highly-alkaline", "highly alkaline", "very-alkaline", "very alkaline", "very_alkaline"),
        INVALID;

        private final List<String> synonyms;

        Acidity(String... synonyms) {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
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

    enum Nutrients implements SynonymEnum<Nutrients> {
        VERY_LOW("scarce", "poor"),
        LOW,
        MEDIUM("normal", "standard", "default"),
        HIGH,
        VERY_HIGH("rich"),
        INVALID;

        private final List<String> synonyms;

        Nutrients(String... synonyms) {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            builder.add(this.name());
            builder.add(synonyms);
            this.synonyms = builder.build();
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

    interface SynonymEnum<T extends Enum<T>> {
        List<String> getSynonyms();

        boolean isValid();

        default boolean isSynonym(String string) {
            return this.getSynonyms().stream().anyMatch(string::equalsIgnoreCase);
        }
    }
}
