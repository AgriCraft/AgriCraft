package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ModSeeds {

	public static final ResourceKey<Registry<AgriSeed>> AGRISEEDS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "seed"));

}
