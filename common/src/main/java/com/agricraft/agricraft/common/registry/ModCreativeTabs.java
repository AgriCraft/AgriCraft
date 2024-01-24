package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeTabs {

	public static final PlatformRegistry<CreativeModeTab> CREATIVE_MODE_TAB = Platform.get().createRegistry(BuiltInRegistries.CREATIVE_MODE_TAB, AgriApi.MOD_ID);

	public static final PlatformRegistry.Entry<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TAB.register("main", Platform.get()::createMainCreativeTab);
	public static final PlatformRegistry.Entry<CreativeModeTab> SEED_TAB = CREATIVE_MODE_TAB.register("seeds", Platform.get()::createSeedsCreativeTab);

}
