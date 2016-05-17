package com.infinityraider.agricraft.reference;

import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;

/**
 * A class containing AgriCraft block states.
 */
public interface AgriCraftProperties {
	
	PropertyBool CROSSCROP = PropertyBool.create("crosscrop");
	PropertyBool WEEDS = PropertyBool.create("weeds");
    PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);
    PropertyCropPlant PLANT = PropertyCropPlant.create("plant");

}
