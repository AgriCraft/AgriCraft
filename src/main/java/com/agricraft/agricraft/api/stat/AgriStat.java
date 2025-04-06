package com.agricraft.agricraft.api.stat;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.genetic.GeneStat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class AgriStat {

	public static ResourceKey<Registry<AgriStat>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("agricraft", "stat"));

	private final IntSupplier min;
	private final IntSupplier max;
	private final BooleanSupplier hidden;
	private final int color;

	public AgriStat(IntSupplier min, IntSupplier max, BooleanSupplier hidden, int color) {
		this.min = min;
		this.max = max;
		this.hidden = hidden;
		this.color = color;
	}

	public int getMin() {
		return this.min.getAsInt();
	}

	public int getMax() {
		return this.max.getAsInt();
	}

	public boolean isHidden() {
		return this.hidden.getAsBoolean();
	}

	public int getColor() {
		return color;
	}

	public void addTooltip(Consumer<Component> consumer, int value) {
		if(!this.isHidden()) {
			consumer.accept(LangUtils.statName(this)
					.append(": " + value)
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	public ResourceLocation getId() {
		return AgriApi.get().getStatRegistry().getKey(this);
	}

	public GeneStat getGene() {
		return (GeneStat) AgriApi.get().getGeneRegistry().get(this.getId());
	}

}
