package com.agricraft.agricraft.api.codecs;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;

public record AgriSoilCondition<T extends AgriSoilValue>(T value, Type type, double toleranceFactor) {

	public static <T extends AgriSoilValue> Codec<AgriSoilCondition<T>> codecForProperty(Codec<T> propertyCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				propertyCodec.fieldOf("value").forGetter(soilCondition -> soilCondition.value),
				Type.CODEC.fieldOf("type").forGetter(soilCondition -> soilCondition.type),
				Codec.DOUBLE.fieldOf("tolerance_factor").forGetter(soilCondition -> soilCondition.toleranceFactor)
		).apply(instance, AgriSoilCondition::new));
	}

	public enum Type {
		EQUAL(i -> i, i -> i),
		EQUAL_OR_LOWER(i -> 0, i -> i),
		EQUAL_OR_HIGHER(i -> i, i -> Integer.MAX_VALUE);

		public static final Codec<Type> CODEC = Codec.STRING.comapFlatMap(s -> Type.fromString(s)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> s + " is no a valid soil value type")),
				type -> type.name().toLowerCase());

		private final IntUnaryOperator lower;
		private final IntUnaryOperator upper;

		Type(IntUnaryOperator lower, IntUnaryOperator upper) {
			this.lower = lower;
			this.upper = upper;
		}

		public static Optional<Type> fromString(String type) {
			return Arrays.stream(values()).filter(value -> value.name().equalsIgnoreCase(type)).findAny();

		}

		public int lowerLimit(int limit) {
			return this.lower.applyAsInt(limit);
		}

		public int upperLimit(int limit) {
			return this.upper.applyAsInt(limit);
		}

	}

	/**
	 * Enum for describing the humidity property of soils
	 */
	public enum Humidity implements AgriSoilValue {
		ARID,
		DRY,
		DAMP("moist"),
		WET("standard", "default"),
		WATERY,
		FLOODED,
		INVALID;

		public static final Codec<Humidity> CODEC = Codec.STRING.comapFlatMap(s -> Humidity.fromString(s)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> s + " is no a valid humidity value")),
				type -> type.name().toLowerCase());

		private final List<String> synonyms;

		Humidity(String... synonyms) {
			this.synonyms = new ImmutableList.Builder<String>().add(this.name()).add(synonyms).build();
		}

		public static Optional<Humidity> fromString(final String string) {
			return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
		}

		@Override
		public boolean isValid() {
			return this != INVALID;
		}

		@Override
		public List<String> synonyms() {
			return this.synonyms;
		}
	}

	/**
	 * Enum for describing the acidity property of soils
	 */
	public enum Acidity implements AgriSoilValue {
		HIGHLY_ACIDIC("0", "1", "2", "highly-acidic", "highly acidic", "very-acidic", "very acidic", "very_acidic"),
		ACIDIC("3", "4", "5"),
		SLIGHTLY_ACIDIC("6", "slightly-acidic", "slightly acidic", "standard", "default"),
		NEUTRAL("7"),
		SLIGHTLY_ALKALINE("8", "slightly-alkaline", "slightly alkaline"),
		ALKALINE("9", "10", "11"),
		HIGHLY_ALKALINE("12", "13", "14", "highly-alkaline", "highly alkaline", "very-alkaline", "very alkaline", "very_alkaline"),
		INVALID;

		public static final Codec<Acidity> CODEC = Codec.STRING.comapFlatMap(s -> Acidity.fromString(s)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> s + " is no a valid acidity value")),
				type -> type.name().toLowerCase());

		private final List<String> synonyms;

		Acidity(String... synonyms) {
			this.synonyms = new ImmutableList.Builder<String>().add(this.name()).add(synonyms).build();
		}

		public static Optional<Acidity> fromString(final String string) {
			return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
		}

		@Override
		public boolean isValid() {
			return this != INVALID;
		}

		@Override
		public List<String> synonyms() {
			return this.synonyms;
		}
	}

	/**
	 * Enum for describing the nutrients property of soils
	 */
	public enum Nutrients implements AgriSoilValue {
		NONE("zero", "empty"),
		VERY_LOW("scarce", "poor"),
		LOW,
		MEDIUM("normal", "average"),
		HIGH("standard", "default"),
		VERY_HIGH("rich"),
		INVALID;

		public static final Codec<Nutrients> CODEC = Codec.STRING.comapFlatMap(s -> Nutrients.fromString(s)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error(() -> s + " is no a valid nutrients value")),
				type -> type.name().toLowerCase());

		private final List<String> synonyms;

		Nutrients(String... synonyms) {
			this.synonyms = new ImmutableList.Builder<String>().add(this.name()).add(synonyms).build();
		}

		public static Optional<Nutrients> fromString(final String string) {
			return Arrays.stream(values()).filter(value -> value.isSynonym(string)).findAny();
		}

		@Override
		public boolean isValid() {
			return this != INVALID;
		}

		@Override
		public List<String> synonyms() {
			return this.synonyms;
		}
	}

}
