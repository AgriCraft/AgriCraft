package com.infinityraider.agricraft.tiles.decoration;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityGrate extends TileEntityCustomWood implements IDebuggable {

    private static final double WIDTH = 2 * Constants.UNIT;
    private static final double LENGTH = 1;

    @Nonnull
    private EnumFacing.Axis axis = EnumFacing.Axis.X;
    @Nonnull
    private EnumOffset offset = EnumOffset.NEAR;
    @Nonnull
    private EnumVines vines = EnumVines.NONE;

    public TileEntityGrate() {
        super();
    }
    
    public boolean hasVines(boolean front) {
        return this.vines.hasVines(front);
    }
    
    @Nonnull
    public EnumFacing.Axis getAxis() {
        return this.axis;
    }
    
    @Nonnull
    public EnumOffset getOffset() {
        return this.offset;
    }
    
    @Nonnull
    public EnumVines getVines() {
        return this.vines;
    }
    
    @Nonnull
    public AxisAlignedBB getBounds() {
        return this.offset.getBounds(this.axis);
    }
    
    public void setAxis(@Nonnull EnumFacing.Axis axis) {
        // Validate.
        Preconditions.checkNotNull(axis);

        // Set.
        if (this.axis != axis) {
            this.axis = axis;
            this.markForUpdate();
        }
    }

    public void setOffset(@Nonnull EnumOffset offset) {
        // Validate.
        Preconditions.checkNotNull(offset);
        
        // Update.
        if (this.offset != offset) {
            this.offset = offset;
            this.markForUpdate();
        }
    }
    
    public boolean setVines(@Nonnull EnumVines vines) {
        // Validate
        Preconditions.checkNotNull(vines);
        
        // Set
        if (this.vines != vines) {
            this.vines = vines;
            this.markForUpdate();
            return true;
        }
        
        // Otherwise return false.
        return false;
    }
    
    public boolean addVines(boolean front) {
        // Delegate.
        return this.setVines(this.vines.addVines(front));
    }

    public boolean removeVines(boolean front) {
        // Delegate.
        return this.setVines(this.vines.removeVines(front));
    }
    
    public boolean isPlayerInFront(EntityPlayer player) {
        return (player != null)
                && (this.axis == player.getHorizontalFacing().getAxis())
                && (player.getHorizontalFacing().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE);
    }

    @Override
    protected void writeNBT(NBTTagCompound tag) {
        tag.setShort(AgriNBT.META, (short) this.offset.ordinal());
        tag.setShort(AgriNBT.VINE, (short) this.vines.ordinal());
        tag.setShort(AgriNBT.AXIS, (short) this.axis.ordinal());
    }

    //this loads the saved data for the tile entity
    @Override
    protected void readNBT(NBTTagCompound tag) {
        this.offset = EnumOffset.values()[tag.getShort(AgriNBT.META) % EnumOffset.values().length];
        this.vines = EnumVines.values()[tag.getShort(AgriNBT.VINE) % EnumVines.values().length];
        this.axis = EnumFacing.Axis.values()[tag.getShort(AgriNBT.AXIS) % EnumFacing.Axis.values().length];
    }

    //debug info
    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        consumer.accept("Grate:");
        super.addServerDebugInfo(consumer);
        consumer.accept(" - Offset: " + this.offset);
        consumer.accept(" - Axis  : " + this.axis);
        consumer.accept(" - Bounds: ");
        consumer.accept("   - x: " + this.getBounds().minX + " - " + this.getBounds().maxX);
        consumer.accept("   - y: " + this.getBounds().minY + " - " + this.getBounds().maxY);
        consumer.accept("   - z: " + this.getBounds().minZ + " - " + this.getBounds().maxZ);
    }

    public static enum EnumVines implements IStringSerializable {
        NONE(false, false),
        FRONT(true, true),
        BACK(true, false),
        BOTH(true, true);

        private final boolean vines;
        private final boolean front;

        EnumVines(boolean hasVines, boolean front) {
            this.vines = hasVines;
            this.front = front;
        }

        public boolean hasVines(boolean front) {
            return this.vines && this.front == front;
        }

        public EnumVines addVines(boolean front) {
            switch (this) {
                case NONE:
                    return (front) ? FRONT : BACK;
                case FRONT:
                    return (front) ? FRONT : BOTH;
                case BACK:
                    return (front) ? BOTH : BACK;
                case BOTH:
                    return BOTH;
            }
            throw new AssertionError();
        }

        public EnumVines removeVines(boolean front) {
            switch (this) {
                case NONE:
                    return NONE;
                case FRONT:
                    return (front) ? NONE : FRONT;
                case BACK:
                    return (front) ? BACK : NONE;
                case BOTH:
                    return (front) ? BACK : FRONT;
            }
            throw new AssertionError();
        }

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }

    public static enum EnumOffset implements IStringSerializable {
        NEAR(0 * 7 * Constants.UNIT),
        CENTER(1 * 7 * Constants.UNIT),
        FAR(2 * 7 * Constants.UNIT);

        private final float offset;
        private final AxisAlignedBB boundsX;
        private final AxisAlignedBB boundsY;
        private final AxisAlignedBB boundsZ;

        EnumOffset(float offset) {
            this.offset = offset;
            this.boundsX = new AxisAlignedBB(offset, 0, 0, WIDTH + offset, LENGTH, LENGTH);
            this.boundsY = new AxisAlignedBB(0, offset, 0, LENGTH, WIDTH + offset, LENGTH);
            this.boundsZ = new AxisAlignedBB(0, 0, offset, LENGTH, LENGTH, WIDTH + offset);
        }

        public float getOffset() {
            return this.offset;
        }

        @Nonnull
        public AxisAlignedBB getBounds(@Nonnull EnumFacing.Axis axis) {
            switch (axis) {
                case X:
                    return boundsX;
                case Y:
                    return boundsY;
                case Z:
                    return boundsZ;
                default:
                    throw new NullPointerException();
            }
        }

        @Override
        @Nonnull
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
