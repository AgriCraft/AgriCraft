package com.InfinityRaider.AgriCraft.reference;

import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;

/**
 * A class containing AgriCraft block states.
 */
public interface AgriCraftBlockStates {
	
	PropertyBool CROSSCROP = PropertyBool.create("CROSSCROP");
	PropertyBool WEEDS = PropertyBool.create("WEEDS");
    PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);
    PropertyCropPlant PLANT = PropertyCropPlant.create("PLANT");

}
