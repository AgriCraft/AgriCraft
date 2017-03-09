package com.infinityraider.agricraft.tiles.decoration;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

import com.infinityraider.agricraft.reference.AgriNBT;

public class TileEntityGrate extends TileEntityCustomWood implements IDebuggable {

	private static final double WIDTH = 2 * Constants.UNIT;
	private static final double LENGTH = 1;

	private EnumOffset offset = EnumOffset.NEAR;
	private EnumVines vines = EnumVines.NONE;
    private EnumFacing.Axis axis = EnumFacing.Axis.X;
	private double[] bounds;

	public TileEntityGrate() {
		super();
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
		calculateBounds();
	}

    public EnumOffset getOffset() {
        return this.offset;
    }

	public TileEntityGrate setOffSet(EnumOffset offSet) {
        if(offSet != this.getOffset()) {
            this.offset = offSet;
            this.markForUpdate();
        }
        return this;
	}

    public EnumFacing.Axis getAxis() {
        return this.axis;
    }

    public TileEntityGrate setAxis(EnumFacing.Axis axis) {
        if(axis != this.getAxis()) {
            this.axis = axis;
            this.markForUpdate();
        }
        return this;
    }

    public TileEntityGrate setAxis(EnumFacing facing) {
        return facing == null ? this : this.setAxis(facing.getAxis());
    }

    public EnumVines getVines() {
        return this.vines;
    }

    public TileEntityGrate setVines(EnumVines vines) {
        if(vines != this.getVines()) {
            this.vines = vines;
            this.markForUpdate();
        }
        return this;
    }

    public boolean hasVines(boolean front) {
        return this.getVines().hasVines(front);
    }

    public boolean addVines(boolean front) {
        EnumVines vines = this.getVines().addVines(front);
        if(vines != this.getVines()) {
            this.setVines(vines);
            return true;
        }
        return false;
    }

    public boolean removeVines(boolean front) {
        EnumVines vines = this.getVines().removeVines(front);
        if(vines != this.getVines()) {
            this.setVines(vines);
            return true;
        }
        return false;
    }

    public void calculateBounds() {
        float offset = this.getOffset().getOffset();
        if (null != this.getOrientation()) switch (this.getOrientation()) {
            default:
            case NORTH:
                this.bounds = new double[]{0, 0, offset, LENGTH, LENGTH, offset + WIDTH};
                break;
            case EAST:
                this.bounds = new double[]{offset, 0, 0, offset + WIDTH, LENGTH, LENGTH};
                break;
            case DOWN:
                this.bounds = new double[]{0, offset, 0, LENGTH, offset + WIDTH, LENGTH};
                break;
        }
    }

	public boolean isPlayerInFront(EntityPlayer player) {
        float offset = this.getOffset().getOffset();
		switch (this.getOrientation()) {
			default:
			case NORTH:
				return player.posZ < this.zCoord() + offset + Constants.UNIT;
			case EAST:
				return player.posX < this.xCoord() + offset + Constants.UNIT;
			case DOWN:
				return player.posY < this.yCoord() + offset + Constants.UNIT;
		}
	}

	public AxisAlignedBB getBoundingBox() {
        float offset = this.getOffset().getOffset();
		switch (this.getOrientation()) {
			default:
			case NORTH:
				return new AxisAlignedBB(xCoord(), yCoord(), zCoord() + offset, xCoord() + LENGTH, yCoord() + LENGTH, zCoord() + offset + WIDTH);
			case EAST:
				return new AxisAlignedBB(xCoord() + offset, yCoord(), zCoord(), xCoord() + offset + WIDTH, yCoord() + LENGTH, zCoord() + LENGTH);
			case DOWN:
				return new AxisAlignedBB(xCoord(), yCoord() + offset, zCoord(), xCoord() + LENGTH, yCoord() + offset + WIDTH, zCoord() + LENGTH);
		}
	}

	public double[] getBlockBounds() {
		return bounds;
	}

	//debug info
	@Override
	public void addServerDebugInfo(List<String> list) {
		list.add("GRATE:");
		super.addServerDebugInfo(list);
		list.add("Offset: " + offset);
		list.add("Orientation: " + getOrientation() + " (" + (getOrientation() == EnumFacing.NORTH ? "xy" : getOrientation() == EnumFacing.EAST ? "zy" : "xz") + ")");
		list.add("Bounds: ");
		list.add(" - x: " + bounds[0] + " - " + bounds[3]);
		list.add(" - y: " + bounds[1] + " - " + bounds[4]);
		list.add(" - z: " + bounds[2] + " - " + bounds[5]);
	}

    public enum EnumVines implements IStringSerializable {
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
            if(this.hasVines(front)) {
                return this;
            }
            return values()[this.ordinal() + (front ? 1 : 2)];
        }

        public EnumVines removeVines(boolean front) {
            if(!this.hasVines(front)) {
                return this;
            }
            return values()[this.ordinal() - (front ? 1 : 2)];
        }

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }

    public enum EnumOffset implements IStringSerializable {
        NEAR(0 * 7 * Constants.UNIT),
        CENTER(1 * 7 * Constants.UNIT),
        FAR(2 * 7 * Constants.UNIT);

        private final float offset;

        EnumOffset(float offset) {
            this.offset = offset;
        }

        public float getOffset() {
            return this.offset;
        }

        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
