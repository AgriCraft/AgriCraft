package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Platform {

	private static Platform platform = null;

	public static void setup(Platform platform) {
		if (Platform.platform == null) {
			Platform.platform = platform;
		}
	}

	public static Platform get() {
		return Platform.platform;
	}

//	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
//		throw new NotImplementedException();
//	}
//
//	public static CreativeModeTab createMainCreativeTab() {
//		throw new NotImplementedException();
//	}
//
//	public static CreativeModeTab createSeedsCreativeTab() {
//		throw new NotImplementedException();
//	}
//
//	public static <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey) {
//		throw new NotImplementedException();
//	}
//
//	public static List<Item> getItemsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
//		throw new NotImplementedException();
//	}
//
//	public static List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag) {
//		throw new NotImplementedException();
//	}
//
//	public static List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
//		throw new NotImplementedException();
//	}
//
//	public static <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
//		throw new NotImplementedException();
//	}
//
//	public static void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
//		throw new NotImplementedException();
//	}

	public abstract <T> PlatformRegistry<T> createRegistry(Registry<T> registry, String modid);

	public abstract AgriSeedItem createAgriSeedItem(Item.Properties properties);

	public abstract CreativeModeTab createMainCreativeTab();

	public abstract CreativeModeTab createSeedsCreativeTab();

	public abstract  <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey);

	public abstract List<Item> getItemsFromLocation(ExtraCodecs.TagOrElementLocation tag);

	public abstract List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag);

	public abstract List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag);

	public Stream<ResourceLocation> getPlantIdsFromTag(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return Stream.of(tag.id());
		} else {
			return AgriApi.getPlantRegistry().flatMap(registry ->
							registry.getTag(TagKey.create(AgriApi.AGRIPLANTS, tag.id()))
									.map(named -> named.stream().map(holder -> registry.getKey(holder.value())))
					)
					.orElse(Stream.empty());
		}
	}

	public abstract  <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

	public abstract void openMenu(ServerPlayer player, ExtraDataMenuProvider provider);

	public abstract ParticleType<?> getParticleType(ResourceLocation particleId);

	@FunctionalInterface
	public interface MenuFactory<T extends AbstractContainerMenu> {

		/**
		 * @param syncId    The internal id for the menu.
		 * @param inventory The inventory of the player.
		 * @param byteBuf   The extra packet data for the menu.
		 * @return The created menu instance.
		 */
		T create(int syncId, Inventory inventory, FriendlyByteBuf byteBuf);

	}

}
