package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeTabs {

	public static final ResourcefulRegistry<CreativeModeTab> CREATIVE_MODE_TAB = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, AgriCraft.MOD_ID);

	public static final RegistryEntry<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TAB.register("main", PlatformUtils::createMainCreativeTab);
	public static final RegistryEntry<CreativeModeTab> SEED_TAB = CREATIVE_MODE_TAB.register("seeds", PlatformUtils::createSeedsCreativeTab);

}
