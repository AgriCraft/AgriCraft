package com.agricraft.agricraft.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.config.CoreConfig;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.synchronization.brigadier.StringArgumentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.world.level.block.ComposterBlock;

public class AgriCraftFabric implements ModInitializer {

	public static MinecraftServer cachedServer;

	@Override
	public void onInitialize() {
		AgriCraft.init();
		DynamicRegistries.registerSynced(PlatformUtils.getPlantRegistryKey(), AgriPlant.CODEC, AgriPlant.CODEC);
		DynamicRegistries.registerSynced(PlatformUtils.AGRISOILS, AgriSoil.CODEC, AgriSoil.CODEC);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> cachedServer = player.getServer());
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			GiveSeedCommand.register(dispatcher, registryAccess);
		});
		MinecraftPlugin.init();
	}

}
