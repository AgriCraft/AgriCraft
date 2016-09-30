package com.infinityraider.agricraft.reference;

import com.infinityraider.agricraft.blocks.properties.PropertyCustomWood;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityTank;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * A class containing AgriCraft block state properties
 */
public interface AgriProperties {

    InfinityProperty<Boolean> JOURNAL = new InfinityProperty<>(PropertyBool.create("journal"), true);
    InfinityProperty<EnumFacing> FACING = new InfinityProperty<>(PropertyDirection.create("facing"), EnumFacing.NORTH);

    InfinityProperty<Boolean> CROSSCROP = new InfinityProperty<>(PropertyBool.create("crosscrop"), false);
    InfinityProperty<Integer> GROWTHSTAGE = new InfinityProperty<>(PropertyInteger.create("age", 0, 7), 0);

    InfinityProperty<Integer> VINES = new InfinityProperty<>(PropertyInteger.create("age", 0, 3), 0);
    InfinityProperty<Boolean> POWERED = new InfinityProperty<>(PropertyBool.create("powered"), false);
    
    InfinityProperty<Boolean> CHANNEL_NORTH = new InfinityProperty<>(PropertyBool.create("channel_north"), false);
    InfinityProperty<Boolean> CHANNEL_EAST = new InfinityProperty<>(PropertyBool.create("channel_east"), false);
    InfinityProperty<Boolean> CHANNEL_SOUTH = new InfinityProperty<>(PropertyBool.create("channel_south"), false);
    InfinityProperty<Boolean> CHANNEL_WEST = new InfinityProperty<>(PropertyBool.create("channel_west"), false);
    
    InfinityProperty<TileEntityTank.Connection> TANK_NORTH = new InfinityProperty<>(PropertyEnum.create("tank_north", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);
    InfinityProperty<TileEntityTank.Connection> TANK_EAST = new InfinityProperty<>(PropertyEnum.create("tank_east", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);
    InfinityProperty<TileEntityTank.Connection> TANK_SOUTH = new InfinityProperty<>(PropertyEnum.create("tank_south", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);
    InfinityProperty<TileEntityTank.Connection> TANK_WEST = new InfinityProperty<>(PropertyEnum.create("tank_west", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);
    InfinityProperty<TileEntityTank.Connection> TANK_UP = new InfinityProperty<>(PropertyEnum.create("tank_up", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);
    InfinityProperty<TileEntityTank.Connection> TANK_DOWN = new InfinityProperty<>(PropertyEnum.create("tank_down", TileEntityTank.Connection.class), TileEntityTank.Connection.NONE);

    IUnlistedProperty<String> PLANT_ID = new PropertyString("plant_id");
    IUnlistedProperty<CustomWoodType> CUSTOM_WOOD_TYPE = new PropertyCustomWood("wood_type");

}
