package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.SeedAnalyzerBlock;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
	public static final PlatformRegistry<Block> BLOCKS = Platform.get().createRegistry(BuiltInRegistries.BLOCK, AgriApi.MOD_ID);

	public static final PlatformRegistry.Entry<Block> CROP = BLOCKS.register("crop", CropBlock::new);
	public static final PlatformRegistry.Entry<Block> SEED_ANALYZER = BLOCKS.register("seed_analyzer", SeedAnalyzerBlock::new);

}
