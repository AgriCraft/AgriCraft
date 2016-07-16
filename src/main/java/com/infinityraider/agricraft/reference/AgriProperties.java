package com.infinityraider.agricraft.reference;

import com.infinityraider.agricraft.utility.AxisPosition;
import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;

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
	PropertyBool PLANT = PropertyBool.create("plant");
    PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);

	PropertyInteger VINES = PropertyInteger.create("vines", 0, 3);
	PropertyEnum<AxisPosition> AXIS_POS = PropertyEnum.create("axis_pos", AxisPosition.class);

	PropertyEnum<WoodType> WOOD_TYPE = PropertyEnum.create("wood_type", WoodType.class);
	
	// Tank
	PropertyInteger NORTH = PropertyInteger.create("north", 0, 2);
	PropertyInteger EAST = PropertyInteger.create("east", 0, 2);
	PropertyInteger SOUTH = PropertyInteger.create("south", 0, 2);
	PropertyInteger WEST = PropertyInteger.create("west", 0, 2);

}
