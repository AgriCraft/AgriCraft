package com.agricraft.agricraft.common.util;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.Optional;

public class PlatformUtils {

	@ExpectPlatform
	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static CreativeModeTab createMainCreativeTab() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static CreativeModeTab createSeedsCreativeTab() {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Item> getItemsFromTag(ResourceLocation tag) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
		throw new NotImplementedException();
	}

	@ExpectPlatform
	public static void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
		throw new NotImplementedException();
	}

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
