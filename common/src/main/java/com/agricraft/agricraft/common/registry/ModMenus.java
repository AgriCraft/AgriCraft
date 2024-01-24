package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.inventory.container.SeedAnalyzerMenu;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

	public static final PlatformRegistry<MenuType<?>> MENUS = Platform.get().createRegistry(BuiltInRegistries.MENU, AgriApi.MOD_ID);

	public static final PlatformRegistry.Entry<MenuType<SeedAnalyzerMenu>> SEED_ANALYZER_MENU = MENUS.register("seed_analyzer",
			() -> Platform.get().createMenuType((id, inv, data) -> new SeedAnalyzerMenu(id, inv, inv.player, data.readBlockPos())));

}
