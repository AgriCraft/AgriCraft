package com.infinityraider.agricraft.init;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.blocks.*;
import com.infinityraider.agricraft.compat.CompatibilityHandler;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.block.Block;

public class AgriCraftBlocks {
	
	@AgriConfigurable(
			category = AgriConfigCategory.DECORATION,
			key = "Enable Fences",
			comment = "Set to false to disable the decorative custom wood fences."
	)
	public static boolean enableFences = true;
	@AgriConfigurable(
			category = AgriConfigCategory.DECORATION,
			key = "Enable Grates",
			comment = "Set to false to disable the decorative custom wood grates."
	)
	public static boolean enableGrates = true;
	
	static {
		AgriCore.getConfig().addConfigurable(AgriCraftBlocks.class);
	}

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
		if (!AgriCraftConfig.disableIrrigation) {
			blockWaterTank = new BlockWaterTank();
			blockWaterChannel = new BlockWaterChannel();
			blockWaterChannelFull = new BlockWaterChannelFull();
			blockChannelValve = new BlockChannelValve();
			blockSprinkler = new BlockSprinkler();
		}
		if (!AgriCraftConfig.disableSeedStorage) {
			blockSeedStorage = new BlockSeedStorage();
			if (!AgriCraftConfig.disableSeedWarehouse) {
				//blockSeedStorageController = new BlockSeedStorageController();
			}
		}
		// The following needs to be done elsewhere.
		// blockPeripheral = new BlockPeripheral();
		if (enableFences) {
			blockFence = new BlockFence();
			blockFenceGate = new BlockFenceGate();
		}
		if (enableGrates) {
			blockGrate = new BlockGrate();
		}
		AgriCore.getLogger("AgriCraft").debug("Blocks registered");
	}
}
