package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.*;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AgriBlocks {

	public static final BlockBase blockCrop = new BlockCrop();
	public static final BlockBase blockSeedAnalyzer = new BlockSeedAnalyzer();
	public static final BlockBase blockWaterPad = new BlockWaterPad();
	public static final BlockBase blockWaterPadFull = new BlockWaterPadFull();
	public static final BlockBase blockWaterTank = new BlockWaterTank();
	public static final BlockBase blockWaterChannel = new BlockWaterChannel();
	public static final BlockBase blockWaterChannelFull = new BlockWaterChannelFull();
	public static final BlockBase blockChannelValve = new BlockChannelValve();
	public static final BlockBase blockSprinkler = new BlockSprinkler();
	public static final BlockBase blockSeedStorage = new BlockSeedStorage();
	//public static final BlockBase blockSeedStorageController = new BlockSeedStorageController();
	// The following is to be done elsewhere...
	//public static final BlockBase blockPeripheral = new BlockPeripheral();
	public static final BlockBase blockFence = new BlockFence();
	public static final BlockBase blockFenceGate = new BlockFenceGate();
	public static final BlockBase blockGrate = new BlockGrate();

	public static void init() {

		// Fetch the logger
		final AgriLogger logger = AgriCore.getLogger("AgriCraft-Blocks");

		// Notify Log
		logger.debug("Starting Block Initialization...");

		// Configure the Blocks
		logger.debug("Starting Block Configuration...");
		ReflectionHelper.forEachIn(AgriBlocks.class, BlockBase.class, (BlockBase block) -> {
			logger.debug("Configuring Block: {0}", block.internalName);
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
