package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.block.CropBlock;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
	public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, AgriCraft.MOD_ID);

	public static final RegistryEntry<Block> CROP = BLOCKS.register("crop", () -> new CropBlock());

}
