package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.neoforge.NeoForgeAgriSeedItem;
import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.ExtraDataMenuProvider;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * NeoForge implementation of {@link Platform}
 */
public class NeoForgePlatform extends Platform {

	@Override
	public <T> PlatformRegistry<T> createRegistry(Registry<T> registry, String modid) {
		return new NeoForgeRegistry<>(registry, modid);
	}

	@Override
	public AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new NeoForgeAgriSeedItem(properties);
	}

	@Override
	public CreativeModeTab createMainCreativeTab() {
		return CreativeModeTab.builder()
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems(ModItems::addItemsToTabs)
//				.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
				.build();
	}

	@Override
	public CreativeModeTab createSeedsCreativeTab() {
		return CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() -> new ItemStack(Items.WHEAT_SEEDS))
				.displayItems((itemDisplayParameters, output) -> AgriApi.getPlantRegistry(ServerLifecycleHooks.getCurrentServer().registryAccess())
						.ifPresent(registry -> {
							AgriCraft.LOGGER.info("add seeds in tab: " + registry.stream().count());
							for (Map.Entry<ResourceKey<AgriPlant>, AgriPlant> entry : registry.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
								output.accept(AgriSeedItem.toStack(entry.getValue()));
							}
						}))
				.withTabsBefore(ModCreativeTabs.MAIN_TAB.id())
				.build();
	}

	@Override
	public <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registry(resourceKey);
	}

	@Override
	public List<Item> getItemsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return List.of(BuiltInRegistries.ITEM.get(tag.id()));
		} else {
			return BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM, tag.id()))
					.map(HolderSet.ListBacked::stream)
					.map(str -> str.map(Holder::value))
					.map(Stream::toList)
					.orElse(List.of());
		}
	}

	@Override
	public List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return List.of(BuiltInRegistries.BLOCK.get(tag.id()));
		} else {
			return BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, tag.id()))
					.map(HolderSet.ListBacked::stream)
					.map(str -> str.map(Holder::value))
					.map(Stream::toList)
					.orElse(List.of());
		}
	}

	@Override
	public List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return List.of(BuiltInRegistries.FLUID.get(tag.id()));
		} else {
			return BuiltInRegistries.FLUID.getTag(TagKey.create(Registries.FLUID, tag.id()))
					.map(HolderSet.ListBacked::stream)
					.map(str -> str.map(Holder::value))
					.map(Stream::toList)
					.orElse(List.of());
		}
	}

	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(Platform.MenuFactory<T> factory) {
		return IMenuTypeExtension.create(factory::create);
	}

	@Override
	public void openMenu(ServerPlayer player, ExtraDataMenuProvider provider) {
		player.openMenu(provider, (data) -> provider.writeExtraData(player, data));
	}

	@Override
	public ParticleType<?> getParticleType(ResourceLocation particleId) {
		return BuiltInRegistries.PARTICLE_TYPE.get(particleId);
	}

}
