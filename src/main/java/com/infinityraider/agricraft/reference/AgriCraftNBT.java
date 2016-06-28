/*
 * 
 */
package com.infinityraider.agricraft.reference;

//NBT tags

/**
 * Class for tracking AgriCraftNBT names.
 * 
 * <s>This is fine, as these are never supposed to change, across all classes.</s>
 * Edit: This class leads to too much confusion, and classes loading the wrong things, without permission.
 * 
 * @since 2.0.0
 */
@Deprecated
public interface AgriCraftNBT {

	String CROSS_CROP = "crossCrop";
	String WEED = "weed";
	String DISCOVERED_SEEDS = "discoveredSeeds";
	String CURRENT_PAGE = "currentPage";
	String WOOD = "wood";
	String CONNECTED = "nrTanks";
	String LEVEL = "level";
	String ID = "id";
	String META = "meta";
	String MATERIAL = "material";
	String MATERIAL_META = "matMeta";
	String IS_SPRINKLED = "isSprinkled";
	String POWER = "powerrrr";
	String INVENTORY = "agricraftInv";
	String SIZE = "size";
	String COUNT = "count";
	String SEED = "seed";
	String TAG = "tag";
	String X1 = "agricraftX";
	String Y1 = "agricraftY";
	String Z1 = "agricraftZ";
	String X2 = "agricraftX2";
	String Y2 = "agricraftY2";
	String Z2 = "agricraftZ2";
	String OVERRIDE = "override";
	String FLAG = "ac_Flag";
	String DIRECTION = "direction";
	String MULTI_BLOCK = "agricraft_MultiBlock";
	
}
