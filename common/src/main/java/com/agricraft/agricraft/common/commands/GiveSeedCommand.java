package com.agricraft.agricraft.common.commands;

import com.agricraft.agricraft.api.genetic.AgriAllele;
import com.agricraft.agricraft.api.genetic.AgriGene;
import com.agricraft.agricraft.api.genetic.AgriGenePair;
import com.agricraft.agricraft.api.genetic.AgriGeneRegistry;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GiveSeedCommand {

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_PLANTS = (commandContext, suggestionsBuilder) -> {
		Collection<ResourceLocation> collection = commandContext.getSource().getServer().registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().keySet();
		return SharedSuggestionProvider.suggestResource(collection, suggestionsBuilder);
	};

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_STATS = (commandContext, suggestionsBuilder) -> {
		List<String> statIds = new ArrayList<>();
		for (AgriStat stat : AgriStatRegistry.getInstance()) {
			statIds.add(stat.getId());
		}
		return SharedSuggestionProvider.suggest(statIds, suggestionsBuilder);
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register((Commands.literal("agricraft_seed")
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)))
				.then(Commands.argument("plant", ResourceLocationArgument.id())
						.suggests(SUGGEST_PLANTS)
						.executes(commandContext -> GiveSeedCommand.giveSeed(commandContext.getSource(), ResourceLocationArgument.getId(commandContext, "plant")))
						.then(Commands.literal("all")
								.then(Commands.argument("count", IntegerArgumentType.integer(1, 10))
										.executes(commandContext -> GiveSeedCommand.giveSeed(commandContext.getSource(), ResourceLocationArgument.getId(commandContext, "plant"), IntegerArgumentType.getInteger(commandContext, "count")))
								)
						)
						.then(Commands.literal("distinct")
								.then(Commands.argument("value", StringArgumentType.string())
										.executes(commandContext -> GiveSeedCommand.giveSeed(commandContext.getSource(), ResourceLocationArgument.getId(commandContext, "plant"), StringArgumentType.getString(commandContext, "value")))
								)
						)
				)
		);

	}

	public static int giveSeed(CommandSourceStack source, ResourceLocation plant) {
		ItemStack itemStack = AgriSeedItem.toStack(PlatformUtils.getPlantFromId(plant.toString()));
		if (giveItemStack(itemStack, source.getPlayer(), source.getLevel())) {
			source.sendSuccess(() -> Component.translatable("agricraft.command.seed_default", plant), true);
			return 1;
		}
		return 0;
	}

	public static int giveSeed(CommandSourceStack source, ResourceLocation plant, int value) {
		AgriAllele<String> allele = AgriGeneRegistry.getInstance().getGeneSpecies().getAllele(plant.toString());
		AgriGenome genome = new AgriGenome(new AgriGenePair<>(AgriGeneRegistry.getInstance().getGeneSpecies(), allele),
				AgriStatRegistry.getInstance().stream()
						.sorted(Comparator.comparing(AgriStat::getId))
						.map(stat -> AgriGeneRegistry.getInstance().getGeneStat(stat))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.map(gene -> new AgriGenePair<>(gene, gene.getAllele(value)))
						.toList());
		ItemStack itemStack = AgriSeedItem.toStack(genome);
		if (giveItemStack(itemStack, source.getPlayer(), source.getLevel())) {
			source.sendSuccess(() -> Component.translatable("agricraft.command.seed_all",plant, value), true);
			return 1;
		}
		return 0;
	}

	private static int giveSeed(CommandSourceStack source, ResourceLocation plant, String distincts) {
		AgriAllele<String> allele = AgriGeneRegistry.getInstance().getGeneSpecies().getAllele(plant.toString());
		List<AgriGene<Integer>> genes = AgriStatRegistry.getInstance().stream()
				.sorted(Comparator.comparing(AgriStat::getId))
				.map(stat -> AgriGeneRegistry.getInstance().getGeneStat(stat))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();

		List<AgriGenePair<Integer>> genePairs = new ArrayList<>();
		String[] split = distincts.split("-");
		int[] values = new int[AgriStatRegistry.getInstance().size()];
		Arrays.fill(values, 1);
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			try {
				values[i] = Mth.clamp(Integer.parseInt(s), 1, 10);
			} catch (NumberFormatException ignored) {
			}
		}
		for (int i = 0; i < genes.size() && i < values.length; i++) {
			AgriGene<Integer> gene = genes.get(i);
			genePairs.add(new AgriGenePair<>(gene, gene.getAllele(values[i])));
		}
		AgriGenome genome = new AgriGenome(new AgriGenePair<>(AgriGeneRegistry.getInstance().getGeneSpecies(), allele), genePairs);
		ItemStack itemStack = AgriSeedItem.toStack(genome);
		if (giveItemStack(itemStack, source.getPlayer(), source.getLevel())) {
			source.sendSuccess(() -> Component.translatable("agricraft.command.seed_distinct", plant, distincts), true);
			return 1;
		}
		return 0;
	}

	private static boolean giveItemStack(ItemStack itemStack, ServerPlayer player, ServerLevel level) {
		boolean added = player.getInventory().add(itemStack);
		if (!added || !itemStack.isEmpty()) {
			ItemEntity itemEntity = player.drop(itemStack, false);
			if (itemEntity == null) {
				return false;
			}
			itemEntity.setNoPickUpDelay();
			itemEntity.setTarget(player.getUUID());
		}
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
		player.containerMenu.broadcastChanges();
		return true;
	}

}
