package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.*;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;

public class AgriCraftBlocks {

	public static Block blockCrop;
	public static Block blockSeedAnalyzer;
	public static Block blockWaterPad;
	public static Block blockWaterPadFull;
	public static Block blockWaterTank;
	public static Block blockWaterChannel;
	public static Block blockWaterChannelFull;
	public static Block blockChannelValve;
	public static Block blockSprinkler;
	public static Block blockSeedStorage;
	public static Block blockSeedStorageController;
	public static Block blockPeripheral;
	public static Block blockFence;
	public static Block blockFenceGate;
	public static Block blockGrate;

	public static void init() {
		blockCrop = new BlockCrop();
		blockSeedAnalyzer = new BlockSeedAnalyzer();
		blockWaterPad = new BlockWaterPad();
		blockWaterPadFull = new BlockWaterPadFull();
		if (!ConfigurationHandler.disableIrrigation) {
			blockWaterTank = new BlockWaterTank();
			blockWaterChannel = new BlockWaterChannel();
			blockWaterChannelFull = new BlockWaterChannelFull();
			blockChannelValve = new BlockChannelValve();
			blockSprinkler = new BlockSprinkler();
		}
		if (!ConfigurationHandler.disableSeedStorage) {
			blockSeedStorage = new BlockSeedStorage();
			if (!ConfigurationHandler.disableSeedWarehouse) {
				//blockSeedStorageController = new BlockSeedStorageController();
			}
		}
		if (CompatibilityHandler.getInstance().isCompatibilityEnabled(AgriCraftMods.computerCraft) || CompatibilityHandler.getInstance().isCompatibilityEnabled(AgriCraftMods.openComputers)) {
			blockPeripheral = new BlockPeripheral();
		}
		if (!ConfigurationHandler.disableFences) {
			blockFence = new BlockFence();
			blockFenceGate = new BlockFenceGate();
		}
		if (!ConfigurationHandler.disableGrates) {
			blockGrate = new BlockGrate();
		}
		LogHelper.debug("Blocks registered");
	}
}
