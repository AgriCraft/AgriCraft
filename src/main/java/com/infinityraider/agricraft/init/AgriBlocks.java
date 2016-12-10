package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.irrigation.BlockSprinkler;
import com.infinityraider.agricraft.blocks.pad.BlockWaterPadFull;
import com.infinityraider.agricraft.blocks.pad.BlockWaterPad;
import com.infinityraider.agricraft.blocks.analyzer.BlockSeedAnalyzer;
import com.infinityraider.agricraft.blocks.storage.BlockSeedStorage;
import com.infinityraider.agricraft.blocks.decoration.BlockFenceGate;
import com.infinityraider.agricraft.blocks.decoration.BlockGrate;
import com.infinityraider.agricraft.blocks.decoration.BlockFence;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelValve;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannel;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.blocks.*;
import com.infinityraider.infinitylib.block.BlockBase;

public class AgriBlocks {
	private static final AgriBlocks INSTANCE = new AgriBlocks();

	public static AgriBlocks getInstance() {
		return INSTANCE;
	}

	private AgriBlocks() {
		CROP = new BlockCrop();
		SEED_ANALYZER = new BlockSeedAnalyzer();
		WATER_PAD = new BlockWaterPad();
		WATER_PAD_FULL = new BlockWaterPadFull();
		TANK = new BlockWaterTank();
		CHANNEL = new BlockWaterChannel();
		CHANNEL_FULL = new BlockWaterChannelFull();
		CHANNEL_VALVE = new BlockWaterChannelValve();
		SPRINKLER = new BlockSprinkler();
		//SEED_STORAGE = new BlockSeedStorage();
		FENCE = new BlockFence();
		FENCE_GATE= new BlockFenceGate();
		GRATE = new BlockGrate();
	}

	// Crops
	public final BlockBase CROP;
	
	// Analyzers
	public final BlockBase SEED_ANALYZER;
	
	// Water Pads
	public final BlockBase WATER_PAD;
	public final BlockBase WATER_PAD_FULL;
	
	// Irrigation
	public final BlockBase TANK;
	public final BlockBase CHANNEL;
	public final BlockBase CHANNEL_FULL;
	public final BlockBase CHANNEL_VALVE;
	public final BlockBase SPRINKLER;
	
	// Seed Storage
	//public final BlockBase SEED_STORAGE;
	//public static final BlockBase SEED_STORAGE_CONTROLLER = new BlockSeedStorageController();
	
	// Decoration
	public final BlockBase FENCE;
	public final BlockBase FENCE_GATE;
	public final BlockBase GRATE;
	
}
