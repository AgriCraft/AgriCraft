/*
 * 
 */
package com.infinityraider.agricraft.api.v1.util;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Matrix used to handle the connections associated with a given connectable component.
 *
 * @author Ryan
 */
public class AgriSideMetaMatrix {

    private byte up;
    private byte down;
    private byte north;
    private byte south;
    private byte west;
    private byte east;

    public AgriSideMetaMatrix() {
        this.up = (byte) 0;
        this.down = (byte) 0;
        this.north = (byte) 0;
        this.south = (byte) 0;
        this.west = (byte) 0;
        this.east = (byte) 0;
    }

    public AgriSideMetaMatrix(@Nonnull AgriSideMetaMatrix from) {
        this.up = from.up;
        this.down = from.down;
        this.north = from.north;
        this.south = from.south;
        this.west = from.west;
        this.east = from.east;
    }

    public byte get(@Nonnull EnumFacing side) {
        switch (side) {
            case UP:
                return up;
            case DOWN:
                return down;
            case NORTH:
                return north;
            case SOUTH:
                return south;
            case WEST:
                return west;
            case EAST:
                return east;
            default:
                throw new NullPointerException();
        }
    }

    public void set(@Nonnull EnumFacing side, byte value) {
        switch (side) {
            case UP:
                this.up = value;
                return;
            case DOWN:
                this.down = value;
                return;
            case NORTH:
                this.north = value;
                return;
            case SOUTH:
                this.south = value;
                return;
            case WEST:
                this.west = value;
                return;
            case EAST:
                this.east = value;
                return;
            default:
                throw new NullPointerException();
        }
    }

    public void writeToNbt(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        for (ConnectionSide side : ConnectionSide.values()) {
            tag.setByte(side.id, this.get(side.aliased));
        }
    }

    public void readFromNbt(@Nonnull NBTTagCompound tag) {
        Preconditions.checkNotNull(tag);
        for (ConnectionSide side : ConnectionSide.values()) {
            if (tag.hasKey(side.id)) {
                this.set(side.aliased, tag.getByte(side.id));
            } else {
                this.set(side.aliased, (byte) 0);
            }
        }
    }

    public AgriSideMetaMatrix copy() {
        return new AgriSideMetaMatrix(this);
    }

    @Nonnull
    public <T extends IBlockState> T writeToBlockState(@Nonnull T state) {
        Preconditions.checkNotNull(state);
        for (ConnectionSide side : ConnectionSide.values()) {
            state = side.property.setValue(state, this.get(side.aliased));
        }
        return state;
    }

    public void readFromBlockState(@Nonnull IBlockState state) {
        for (ConnectionSide side : ConnectionSide.values()) {
            this.set(side.aliased, side.property.getValue(state, (byte) 0));
        }
    }

    public void toString(@Nonnull Consumer<String> consumer) {
        // Validate.
        Preconditions.checkNotNull(consumer);

        // Add the header.
        consumer.accept("Side Meta Matrix:");

        // Add lines to the method.
        for (EnumFacing side : EnumFacing.VALUES) {
            consumer.accept(" - " + side + ": " + this.get(side));
        }
    }

    public static void addUnlistedProperties(Consumer<IUnlistedProperty> consumer) {
        for (ConnectionSide side : ConnectionSide.values()) {
            consumer.accept(side.property);
        }
    }

    public static enum ConnectionSide {
        UP(EnumFacing.UP),
        DOWN(EnumFacing.DOWN),
        NORTH(EnumFacing.NORTH),
        SOUTH(EnumFacing.SOUTH),
        WEST(EnumFacing.WEST),
        EAST(EnumFacing.EAST);

        public static final String ID_PREFIX = "agri_side_meta_";

        public final String id;
        public final EnumFacing aliased;
        public final UnlistedPropertyByte property;

        private ConnectionSide(@Nonnull EnumFacing aliased) {
            this.id = ID_PREFIX + this.name().toLowerCase();
            this.aliased = Preconditions.checkNotNull(aliased);
            this.property = new UnlistedPropertyByte(id);
        }

        @Nonnull
        public static ConnectionSide getFor(@Nonnull EnumFacing side) {
            switch (side) {
                case UP:
                    return UP;
                case DOWN:
                    return DOWN;
                case NORTH:
                    return NORTH;
                case SOUTH:
                    return SOUTH;
                case WEST:
                    return WEST;
                case EAST:
                    return EAST;
                default:
                    throw new NullPointerException();
            }
        }

    }

}
