package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.*;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AgriBlocks {

	// Crops
	public static final BlockBase CROP = new BlockCrop();
	
	// Analyzers
	public static final BlockBase SEED_ANALYZER = new BlockSeedAnalyzer();
	// The following is to be done elsewhere...
	//public static final BlockBase PERIPHERAL = new BlockPeripheral();
	
	// Water Pads
	public static final BlockBase WATER_PAD = new BlockWaterPad();
	public static final BlockBase WATER_PAD_FULL = new BlockWaterPadFull();
	
	// Irrigation
	public static final BlockBase TANK = new BlockWaterTank();
	public static final BlockBase CHANNEL = new BlockWaterChannel();
	public static final BlockBase CHANNEL_FULL = new BlockWaterChannelFull();
	public static final BlockBase CHANNEL_VALVE = new BlockChannelValve();
	public static final BlockBase SPRINKLER = new BlockSprinkler();
	
	// Seed Storage
	public static final BlockBase SEED_STORAGE = new BlockSeedStorage();
	//public static final BlockBase SEED_STORAGE_CONTROLLER = new BlockSeedStorageController();
	
	// Decoration
	public static final BlockBase FENCE = new BlockFence();
	public static final BlockBase FENCE_GATE = new BlockFenceGate();
	public static final BlockBase GRATE = new BlockGrate();

	public static void init() {

		// Fetch the logger
		final AgriLogger logger = AgriCore.getLogger("AgriCraft-Blocks");

		// Notify Log
		logger.debug("Starting Block Initialization...");

		// Configure the Blocks
		logger.debug("Starting Block Configuration...");
		ReflectionHelper.forEachIn(AgriBlocks.class, BlockBase.class, (BlockBase block) -> {
			logger.debug("Configuring Block: {0}", block.getInternalName());
			AgriCore.getConfig().addConfigurable(block);
		});
		logger.debug("Finished Block Configuration!");

		// Register the Blocks
		logger.debug("Starting Block Registration...");
		ReflectionHelper.forEachIn(AgriBlocks.class, BlockBase.class, (BlockBase block) -> {
			if (block.isEnabled()) {
				logger.debug("Registering Block: {0}", block.getInternalName());
				RegisterHelper.registerBlock(block, block.getInternalName(), block.getItemBlockClass());
			}
		});
		logger.debug("Finished Block Registration!");
		
		// Register the Tiles
		logger.debug("Starting Tile Registration...");
		ReflectionHelper.forEachIn(AgriBlocks.class, BlockBaseTile.class, (BlockBaseTile block) -> {
			if (block.isEnabled()) {
				logger.debug("Registering Tile for Block: {0}", block.getInternalName());
				TileEntity te = block.createNewTileEntity(null, 0);
				assert (te != null);
				GameRegistry.registerTileEntity(te.getClass(), block.getTileName());
			}
		});
		logger.debug("Finished Tile Registration!");

		// Notify Log
		AgriCore.getLogger("AgriCraft").debug("Finished Block Initialization!");
		
	}
	
}
