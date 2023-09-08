package com.agricraft.agricraft.api.codecs;

import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record AgriSoil(boolean enabled, List<String> mods, List<AgriSoilVariant> variants, AgriSoilValue humidity,
                       AgriSoilValue acidity, AgriSoilValue nutrients, Double growthModifier)  implements MagnifyingInspectable {

	public static final Codec<AgriSoil> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("enabled").forGetter(soil -> soil.enabled),
			Codec.STRING.listOf().fieldOf("mods").forGetter(soil -> soil.mods),
			AgriSoilVariant.CODEC.listOf().fieldOf("variants").forGetter(soil -> soil.variants),
			AgriSoilCondition.Humidity.CODEC.fieldOf("humidity").forGetter(soil -> soil.humidity),
			AgriSoilCondition.Acidity.CODEC.fieldOf("acidity").forGetter(soil -> soil.acidity),
			AgriSoilCondition.Nutrients.CODEC.fieldOf("nutrients").forGetter(soil -> soil.nutrients),
			Codec.DOUBLE.fieldOf("growth_modifier").forGetter(soil -> soil.growthModifier)
	).apply(instance, AgriSoil::new));

	public boolean isVariant(BlockState blockState) {
		for (AgriSoilVariant variant : this.variants) {
			List<Block> blocks = PlatformUtils.getBlocksFromLocation(variant.block());
			if (blocks.contains(blockState.getBlock())) {
				if (variant.states().isEmpty()) {
					return true;
				}
				Set<String> list = blockState.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.toSet());
				if (list.containsAll(variant.states())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		tooltip.add(Component.translatable("agricraft.tooltip.magnifying.soil"));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.soil"))
				.append(Component.translatable("soil.agricraft." + PlatformUtils.getIdFromSoil(this).replace(":", "."))));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.humidity"))
				.append(Component.translatable("agricraft.soil.humidity." + this.humidity.name().toLowerCase())));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.acidity"))
				.append(Component.translatable("agricraft.soil.acidity." + this.acidity.name().toLowerCase())));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.nutrients"))
				.append(Component.translatable("agricraft.soil.nutrients." + this.nutrients.name().toLowerCase())));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		List<String> mods = new ArrayList<>();
		List<AgriSoilVariant> soilVariants = new ArrayList<>();
		AgriSoilValue humidity = AgriSoilCondition.Humidity.WET;
		AgriSoilValue acidity = AgriSoilCondition.Acidity.NEUTRAL;
		AgriSoilValue nutrients = AgriSoilCondition.Nutrients.MEDIUM;
		double growthModifier = 1.0;

		public AgriSoil build() {
			return new AgriSoil(true, mods, soilVariants, humidity, acidity, nutrients, growthModifier);
		}

		public Builder defaultMods() {
			Collections.addAll(this.mods, "agricraft", "minecraft");
			return this;
		}

		public Builder mods(String... mods) {
			Collections.addAll(this.mods, mods);
			return this;
		}

		public Builder variants(AgriSoilVariant... variants) {
			Collections.addAll(this.soilVariants, variants);
			return this;
		}

		public Builder humidity(AgriSoilCondition.Humidity humidity) {
			this.humidity = humidity;
			return this;
		}

		public Builder acidity(AgriSoilCondition.Acidity acidity) {
			this.acidity = acidity;
			return this;
		}

		public Builder nutrients(AgriSoilCondition.Nutrients nutrients) {
			this.nutrients = nutrients;
			return this;
		}

		public Builder growthModifier(double growthModifier) {
			this.growthModifier = growthModifier;
			return this;
		}

	}

}
