package com.infinityraider.agricraft.reference;

import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;

/**
 * A class containing AgriCraft block states.
 * 
 * These are useless since the crop block far exceeds 16 combinations.
 * 
 * @deprecated Deprecated until a better way of using these properties is determined.
 */
@Deprecated
public interface AgriCraftProperties {
	
	PropertyBool CROSSCROP = PropertyBool.create("crosscrop");
	PropertyBool WEEDS = PropertyBool.create("weeds");
    PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);
    PropertyCropPlant PLANT = PropertyCropPlant.create("plant");

}
