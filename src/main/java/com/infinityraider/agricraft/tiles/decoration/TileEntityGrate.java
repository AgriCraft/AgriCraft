package com.infinityraider.agricraft.tiles.decoration;

import com.infinityraider.agricraft.api.v1.misc.IDebuggable;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TileEntityGrate extends TileEntityCustomWood implements IDebuggable {

	private static final double WIDTH = 2 * Constants.UNIT;
	private static final double LENGTH = 1;
	private static final double[] OFFSETS = new double[]{ // The compiler does these calculations at compile time so no need to worry.
		0 * 7 * Constants.UNIT, //offset 0
		1 * 7 * Constants.UNIT, //offset 1
		2 * 7 * Constants.UNIT, //offset 2
	};

	private int offset;
	private int vines; //0: no, 1: front, 2: back, 3: both
	private double[] bounds;

	public TileEntityGrate() {
		super();
	}

	@Override
	public boolean isRotatable() {
		return true;
	}

	@Override
	protected void writeNBT(NBTTagCompound tag) {
		tag.setShort(AgriCraftNBT.META, (short) offset);
		tag.setShort(AgriCraftNBT.WEED, (short) vines);
	}

	//this loads the saved data for the tile entity
	@Override
	protected void readNBT(NBTTagCompound tag) {
		this.offset = tag.getShort(AgriCraftNBT.META);
		calculateBounds();
		this.vines = tag.getShort(AgriCraftNBT.WEED);
	}

	public void calculateBounds() {
		if (null != this.orientation) switch (this.orientation) {
			default:
			case NORTH:
				this.bounds = new double[]{0, 0, OFFSETS[offset], LENGTH, LENGTH, OFFSETS[offset] + WIDTH};
				break;
			case EAST:
				this.bounds = new double[]{OFFSETS[offset], 0, 0, OFFSETS[offset] + WIDTH, LENGTH, LENGTH};
				break;
			case DOWN:
				this.bounds = new double[]{0, OFFSETS[offset], 0, LENGTH, OFFSETS[offset] + WIDTH, LENGTH};
				break;
		}
	}

	public void setOffSet(short value) {
		value = value > 2 ? 2 : value;
		value = value < 0 ? 0 : value;
		if (this.offset == value) {
			return;
		}
		this.offset = value;
		this.markForUpdate();
	}

	public int getOffset() {
		return offset;
	}

	public boolean isPlayerInFront(EntityPlayer player) {
		switch (this.orientation) {
			default:
			case NORTH:
				return player.posZ < this.zCoord() + OFFSETS[offset] + Constants.UNIT;
			case EAST:
				return player.posX < this.xCoord() + OFFSETS[offset] + Constants.UNIT;
			case DOWN:
				return player.posY < this.yCoord() + OFFSETS[offset] + Constants.UNIT;
		}
	}

	public boolean hasVines(boolean front) {
		return vines == 3 || (front ? vines == 1 : vines == 2);
	}

	public boolean addVines(boolean front) {
		if (hasVines(front)) {
			return false;
		} else {
			this.vines = (vines + (front ? 1 : 2));
			this.markForUpdate();
			return true;
		}
	}

	public boolean removeVines(boolean front) {
		if (hasVines(front)) {
			this.vines = Math.max((vines - (front ? 1 : 2)), 0);
			this.markForUpdate();
			return true;
		}
		return false;
	}

	public AxisAlignedBB getBoundingBox() {
		switch (this.orientation) {
			default:
			case NORTH:
				return new AxisAlignedBB(xCoord(), yCoord(), zCoord() + OFFSETS[offset], xCoord() + LENGTH, yCoord() + LENGTH, zCoord() + OFFSETS[offset] + WIDTH);
			case EAST:
				return new AxisAlignedBB(xCoord() + OFFSETS[offset], yCoord(), zCoord(), xCoord() + OFFSETS[offset] + WIDTH, yCoord() + LENGTH, zCoord() + LENGTH);
			case DOWN:
				return new AxisAlignedBB(xCoord(), yCoord() + OFFSETS[offset], zCoord(), xCoord() + LENGTH, yCoord() + OFFSETS[offset] + WIDTH, zCoord() + LENGTH);
		}
	}

	public double[] getBlockBounds() {
		return bounds;
	}

	//debug info
	@Override
	public void addDebugInfo(List<String> list) {
		list.add("GRATE:");
		super.addDebugInfo(list);
		list.add("Offset: " + offset);
		list.add("Orientation: " + orientation + " (" + (orientation == AgriForgeDirection.NORTH ? "xy" : orientation == AgriForgeDirection.EAST ? "zy" : "xz") + ")");
		list.add("Bounds: ");
		list.add(" - x: " + bounds[0] + " - " + bounds[3]);
		list.add(" - y: " + bounds[1] + " - " + bounds[4]);
		list.add(" - z: " + bounds[2] + " - " + bounds[5]);
	}
}
