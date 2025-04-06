package com.agricraft.agricraft.api.codecs;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.TagUtils;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record AgriSoil(List<String> mods, List<AgriSoilVariant> variants,
                       AgriSoilCondition.Humidity humidity,
                       AgriSoilCondition.Acidity acidity, AgriSoilCondition.Nutrients nutrients,
                       Double growthModifier) implements MagnifyingInspectable {

	public static final ResourceKey<Registry<AgriSoil>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "soil"));

	public static final Codec<AgriSoil> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("mods").forGetter(soil -> soil.mods),
			AgriSoilVariant.CODEC.listOf().fieldOf("variants").forGetter(soil -> soil.variants),
			AgriSoilCondition.Humidity.CODEC.fieldOf("humidity").forGetter(soil -> soil.humidity),
			AgriSoilCondition.Acidity.CODEC.fieldOf("acidity").forGetter(soil -> soil.acidity),
			AgriSoilCondition.Nutrients.CODEC.fieldOf("nutrients").forGetter(soil -> soil.nutrients),
			Codec.DOUBLE.fieldOf("growth_modifier").forGetter(soil -> soil.growthModifier)
	).apply(instance, AgriSoil::new));

	public static Builder builder() {
		return new Builder();
	}

	public boolean isVariant(BlockState blockState) {
		for (AgriSoilVariant variant : this.variants) {
			List<Block> blocks = TagUtils.blocks(variant.block());
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

	public boolean isVariant(Item item) {
		return this.variants.stream()
				.flatMap(variant -> TagUtils.blocks(variant.block()).stream())
				.map(Block::asItem)
				.anyMatch(it -> it == item);
	}

	@Override
	public void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		tooltip.add(Component.translatable("agricraft.tooltip.magnifying.soil"));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.soil"))
				.append(LangUtils.soilName(this.getId().map(ResourceLocation::toString).orElse(""))));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.humidity"))
				.append(LangUtils.soilPropertyName("humidity", this.humidity)));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.acidity"))
				.append(LangUtils.soilPropertyName("acidity", this.acidity)));
		tooltip.add(Component.literal("  ")
				.append(Component.translatable("agricraft.tooltip.magnifying.soil.nutrients"))
				.append(LangUtils.soilPropertyName("nutrients", this.nutrients)));
	}

	public Optional<ResourceLocation> getId() {
		return AgriApi.get().getSoilRegistry().map(reg -> reg.getKey(this));
	}

	public static class Builder {

		List<String> mods = new ArrayList<>();
		List<AgriSoilVariant> soilVariants = new ArrayList<>();
		AgriSoilCondition.Humidity humidity = AgriSoilCondition.Humidity.INVALID;
		AgriSoilCondition.Acidity acidity = AgriSoilCondition.Acidity.INVALID;
		AgriSoilCondition.Nutrients nutrients = AgriSoilCondition.Nutrients.INVALID;
		double growthModifier = 1.0;

		public AgriSoil build() {
			return new AgriSoil(mods, soilVariants, humidity, acidity, nutrients, growthModifier);
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
