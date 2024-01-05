package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.inventory.container.SeedAnalyzerMenu;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

	public static final ResourcefulRegistry<MenuType<?>> MENUS = ResourcefulRegistries.create(BuiltInRegistries.MENU, AgriApi.MOD_ID);

	public static final RegistryEntry<MenuType<SeedAnalyzerMenu>> SEED_ANALYZER_MENU = MENUS.register("seed_analyzer",
			() -> PlatformUtils.createMenuType((id, inv, data) -> new SeedAnalyzerMenu(id, inv, inv.player, data.readBlockPos())));

}
