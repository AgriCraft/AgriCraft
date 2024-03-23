package com.agricraft.agricraft.common.commands;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetic.AgriAllele;
import com.agricraft.agricraft.api.genetic.AgriGene;
import com.agricraft.agricraft.api.genetic.AgriGenePair;
import com.agricraft.agricraft.api.genetic.AgriGeneRegistry;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.util.Platform;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Command to dump agricraft registries
 */
public class DumpRegistriesCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(Commands.literal("agricraft_dump")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2))
				.then(Commands.literal("plants").executes(commandContext -> DumpRegistriesCommand.dumpPlants()))
				.then(Commands.literal("soils").executes(commandContext -> DumpRegistriesCommand.dumpSoils()))
				.then(Commands.literal("mutations").executes(commandContext -> DumpRegistriesCommand.dumpMutations()))
				.then(Commands.literal("fertilizers").executes(commandContext -> DumpRegistriesCommand.dumpFertilizers()))
				.executes(commandContext -> DumpRegistriesCommand.dumpAllRegistries())
		);

	}

	public static int dumpAllRegistries() {
		dumpPlants();
		dumpSoils();
		dumpMutations();
		dumpFertilizers();
		return 1;
	}

	public static int dumpPlants() {
		System.out.println("Plants:");
		AgriApi.getPlantRegistry().ifPresent(registry -> registry.keySet().forEach(System.out::println));
		return 1;
	}
	public static int dumpSoils() {
		System.out.println("Soils:");
		AgriApi.getSoilRegistry().ifPresent(registry -> registry.keySet().forEach(System.out::println));
		return 1;
	}
	public static int dumpMutations() {
		System.out.println("Mutations:");
		AgriApi.getMutationRegistry().ifPresent(registry -> registry.keySet().forEach(System.out::println));
		return 1;
	}
	public static int dumpFertilizers() {
		System.out.println("Fertilizers:");
		AgriApi.getFertilizerRegistry().ifPresent(registry -> registry.keySet().forEach(System.out::println));
		return 1;
	}

}
