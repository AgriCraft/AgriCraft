/*
 */
package com.infinityraider.agricraft.api.v1.irrigation;

import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/*
 *
 */
public class IrrigationConnection {

    public static final String NORTH_NAME = "meta_north";
    public static final String EAST_NAME = "meta_east";
    public static final String SOUTH_NAME = "meta_south";
    public static final String WEST_NAME = "meta_west";
    public static final String UP_NAME = "meta_up";
    public static final String DOWN_NAME = "meta_down";

    public static final InfinityProperty<IrrigationConnectionType> NORTH = new InfinityProperty<>(PropertyEnum.create(NORTH_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);
    public static final InfinityProperty<IrrigationConnectionType> EAST = new InfinityProperty<>(PropertyEnum.create(EAST_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);
    public static final InfinityProperty<IrrigationConnectionType> SOUTH = new InfinityProperty<>(PropertyEnum.create(SOUTH_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);
    public static final InfinityProperty<IrrigationConnectionType> WEST = new InfinityProperty<>(PropertyEnum.create(WEST_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);
    public static final InfinityProperty<IrrigationConnectionType> UP = new InfinityProperty<>(PropertyEnum.create(UP_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);
    public static final InfinityProperty<IrrigationConnectionType> DOWN = new InfinityProperty<>(PropertyEnum.create(DOWN_NAME, IrrigationConnectionType.class), IrrigationConnectionType.NONE);

    public static final InfinityProperty<IrrigationConnectionType>[] CONNECTIONS = new InfinityProperty[]{
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN
    };

    private IrrigationConnectionType north;
    private IrrigationConnectionType east;
    private IrrigationConnectionType south;
    private IrrigationConnectionType west;
    private IrrigationConnectionType up;
    private IrrigationConnectionType down;

    public IrrigationConnection() {
        this.north = IrrigationConnectionType.NONE;
        this.east = IrrigationConnectionType.NONE;
        this.south = IrrigationConnectionType.NONE;
        this.west = IrrigationConnectionType.NONE;
        this.up = IrrigationConnectionType.NONE;
        this.down = IrrigationConnectionType.NONE;
    }

    public IrrigationConnectionType getNorth() {
        return north;
    }

    public IrrigationConnectionType getEast() {
        return east;
    }

    public IrrigationConnectionType getSouth() {
        return south;
    }

    public IrrigationConnectionType getWest() {
        return west;
    }

    public IrrigationConnectionType getUp() {
        return up;
    }

    public IrrigationConnectionType getDown() {
        return down;
    }

    public IrrigationConnectionType get(EnumFacing side) {
        switch (side) {
            case NORTH:
                return getNorth();
            case EAST:
                return getEast();
            case SOUTH:
                return getSouth();
            case WEST:
                return getWest();
            case UP:
                return getUp();
            case DOWN:
                return getDown();
            default:
                return IrrigationConnectionType.NONE;
        }
    }

    public void setNorth(IrrigationConnectionType north) {
        if (north != null) {
            this.north = north;
        } else {
            this.north = IrrigationConnectionType.NONE;
        }
    }

    public void setEast(IrrigationConnectionType east) {
        if (east != null) {
            this.east = east;
        } else {
            this.east = IrrigationConnectionType.NONE;
        }
    }

    public void setSouth(IrrigationConnectionType south) {
        if (south != null) {
            this.south = south;
        } else {
            this.south = IrrigationConnectionType.NONE;
        }
    }

    public void setWest(IrrigationConnectionType west) {
        if (west != null) {
            this.west = west;
        } else {
            this.west = IrrigationConnectionType.NONE;
        }
    }

    public void setUp(IrrigationConnectionType up) {
        if (up != null) {
            this.up = up;
        } else {
            this.up = IrrigationConnectionType.NONE;
        }
    }

    public void setDown(IrrigationConnectionType down) {
        if (down != null) {
            this.down = down;
        } else {
            this.down = IrrigationConnectionType.NONE;
        }
    }

    public void set(EnumFacing side, IrrigationConnectionType value) {
        switch (side) {
            case NORTH:
                this.setNorth(value);
                break;
            case EAST:
                this.setEast(value);
                break;
            case SOUTH:
                this.setSouth(value);
                break;
            case WEST:
                this.setWest(value);
                break;
            case UP:
                this.setUp(value);
                break;
            case DOWN:
                this.setDown(value);
                break;
        }
    }

    public void read(NBTTagCompound tag) {
        this.setNorth(IrrigationConnectionType.fromIndex(tag.getByte(NORTH_NAME)));
        this.setEast(IrrigationConnectionType.fromIndex(tag.getByte(EAST_NAME)));
        this.setSouth(IrrigationConnectionType.fromIndex(tag.getByte(SOUTH_NAME)));
        this.setWest(IrrigationConnectionType.fromIndex(tag.getByte(WEST_NAME)));
        this.setUp(IrrigationConnectionType.fromIndex(tag.getByte(UP_NAME)));
        this.setDown(IrrigationConnectionType.fromIndex(tag.getByte(DOWN_NAME)));
    }

    public void read(IBlockState state) {
        this.setNorth(NORTH.getValue(state));
        this.setEast(EAST.getValue(state));
        this.setSouth(SOUTH.getValue(state));
        this.setWest(WEST.getValue(state));
        this.setUp(UP.getValue(state));
        this.setDown(DOWN.getValue(state));
    }

    public void write(NBTTagCompound tag) {
        tag.setByte(NORTH_NAME, (byte) north.ordinal());
        tag.setByte(EAST_NAME, (byte) east.ordinal());
        tag.setByte(SOUTH_NAME, (byte) south.ordinal());
        tag.setByte(WEST_NAME, (byte) west.ordinal());
        tag.setByte(UP_NAME, (byte) up.ordinal());
        tag.setByte(DOWN_NAME, (byte) down.ordinal());
    }

    public IBlockState write(IBlockState state) {
        state = NORTH.applyToBlockState(state, north);
        state = EAST.applyToBlockState(state, east);
        state = SOUTH.applyToBlockState(state, south);
        state = WEST.applyToBlockState(state, west);
        state = UP.applyToBlockState(state, up);
        state = DOWN.applyToBlockState(state, down);
        return state;
    }

}
