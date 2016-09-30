package com.infinityraider.agricraft.reference;

import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * A class containing AgriCraft block state properties, most of these are
 * unlisted
 */
public interface AgriProperties {

    InfinityProperty<Boolean> JOURNAL = new InfinityProperty<>(PropertyBool.create("journal"), true);
    InfinityProperty<EnumFacing> FACING = new InfinityProperty<>(PropertyDirection.create("facing"), EnumFacing.NORTH);

    InfinityProperty<Boolean> CROSSCROP = new InfinityProperty<>(PropertyBool.create("crosscrop"), false);
    InfinityProperty<Integer> GROWTHSTAGE = new InfinityProperty<>(PropertyInteger.create("age", 0, 7), 0);

    InfinityProperty<Integer> VINES = new InfinityProperty<>(PropertyInteger.create("age", 0, 3), 0);

    IUnlistedProperty<String> PLANT_ID = new PropertyString("plant_id");
    
    InfinityProperty<Boolean> CHANNEL_NORTH = new InfinityProperty<>(PropertyBool.create("channel_north"), false);
    InfinityProperty<Boolean> CHANNEL_EAST = new InfinityProperty<>(PropertyBool.create("channel_east"), false);
    InfinityProperty<Boolean> CHANNEL_SOUTH = new InfinityProperty<>(PropertyBool.create("channel_south"), false);
    InfinityProperty<Boolean> CHANNEL_WEST = new InfinityProperty<>(PropertyBool.create("channel_west"), false);
    InfinityProperty<Boolean> CHANNEL_UP = new InfinityProperty<>(PropertyBool.create("channel_up"), false);
    InfinityProperty<Boolean> CHANNEL_DOWN = new InfinityProperty<>(PropertyBool.create("channel_down"), false);
    
    InfinityProperty<Boolean> TANK_NORTH = new InfinityProperty<>(PropertyBool.create("tank_north"), false);
    InfinityProperty<Boolean> TANK_EAST = new InfinityProperty<>(PropertyBool.create("tank_east"), false);
    InfinityProperty<Boolean> TANK_SOUTH = new InfinityProperty<>(PropertyBool.create("tank_south"), false);
    InfinityProperty<Boolean> TANK_WEST = new InfinityProperty<>(PropertyBool.create("tank_west"), false);
    InfinityProperty<Boolean> TANK_UP = new InfinityProperty<>(PropertyBool.create("tank_up"), false);
    InfinityProperty<Boolean> TANK_DOWN = new InfinityProperty<>(PropertyBool.create("tank_down"), false);

}
