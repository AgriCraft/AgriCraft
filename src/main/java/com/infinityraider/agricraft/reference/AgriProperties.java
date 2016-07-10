package com.infinityraider.agricraft.reference;

import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;

/**
 * A class containing AgriCraft block states.
 * 
 * These are useless since the crop block far exceeds 16 combinations.
 * 
 */
public interface AgriProperties {
	
	
	PropertyBool JOURNAL = PropertyBool.create("journal");
    PropertyDirection FACING = PropertyDirection.create("facing");
	
	PropertyBool CROSSCROP = PropertyBool.create("crosscrop");
	PropertyBool WEEDS = PropertyBool.create("weeds");
    PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);
	PropertyCropPlant PLANT = PropertyCropPlant.create("plant");

}
