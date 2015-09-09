package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class TileEntityGrate extends TileEntityCustomWood implements IDebuggable {
    private static final double WIDTH = 2 * Constants.UNIT;
    private static final double LENGTH = 1;
    private static final double[] OFFSETS = new double[]{ // The compiler does these calculations at compile time so no need to worry.
        0 * 7 * Constants.UNIT, //offset 0
        1 * 7 * Constants.UNIT, //offset 1
        2 * 7 * Constants.UNIT, //offset 2
    };

    private int orientation;  //0: xy, 1: zy, 2: xz
    private int offset;
    private int vines; //0: no, 1: front, 2: back, 3: both
    private double[] bounds;

    public TileEntityGrate() {
        super();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setShort(Names.NBT.flag, (short) orientation);
        tag.setShort(Names.NBT.meta, (short) offset);
        tag.setShort(Names.NBT.weed, (short) vines);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.orientation = tag.getShort(Names.NBT.flag);
        this.offset = tag.getShort(Names.NBT.meta);
        calculateBounds();
        this.vines = tag.getShort(Names.NBT.weed);
    }

    public void calculateBounds() {
        if(this.orientation == 0) {
            this.bounds = new double[] {0, 0, OFFSETS[offset], LENGTH, LENGTH, OFFSETS[offset]+WIDTH};
        }
        else if(this.orientation == 1) {
            this.bounds = new double[] {OFFSETS[offset], 0, 0, OFFSETS[offset]+WIDTH, LENGTH, LENGTH};
        }
        else {
            this.bounds = new double[] {0, OFFSETS[offset], 0, LENGTH, OFFSETS[offset]+WIDTH, LENGTH};
        }
    }

    public void setOrientationValue(short value) {
        value = value>2?2:value;
        value = value<0?0:value;
        if(this.orientation==value) {
            return;
        }
        this.orientation = value;
        this.markForUpdate();
    }

    public int getOrientationValue() {
        return orientation;
    }

    public void setOffSet(short value) {
        value = value>2?2:value;
        value = value<0?0:value;
        if(this.offset==value) {
            return;
        }
        this.offset = value;
        this.markForUpdate();
    }

    public int getOffset() {
        return offset;
    }

    public boolean isPlayerInFront(EntityPlayer player) {
        if(this.orientation == 0) {
            return player.posZ < this.zCoord + OFFSETS[offset] + Constants.UNIT;
        }
        else if(this.orientation == 1) {
            return player.posX < this.xCoord + OFFSETS[offset] + Constants.UNIT;
        }
        else {
            return player.posY < this.yCoord + OFFSETS[offset] + Constants.UNIT;
        }
    }

    public boolean hasVines(boolean front) {
        return vines==3 || (front?vines==1:vines==2);
    }

    public boolean addVines(boolean front) {
        if(hasVines(front)) {
            return false;
        } else {
            this.vines = (vines+(front?1:2));
            this.markForUpdate();
            return true;
        }
    }

    public boolean removeVines(boolean front) {
        if(hasVines(front)) {
            this.vines = Math.max((vines - (front ? 1 : 2)), 0);
            this.markForUpdate();
            return true;
        }
        return false;
    }

    public AxisAlignedBB getBoundingBox() {
        if(this.orientation == 0) {
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord+OFFSETS[offset], xCoord+LENGTH, yCoord+LENGTH, zCoord+OFFSETS[offset]+WIDTH);
        }
        else if(this.orientation == 1) {
            return AxisAlignedBB.getBoundingBox(xCoord+OFFSETS[offset], yCoord, zCoord, xCoord+OFFSETS[offset]+WIDTH, yCoord+LENGTH, zCoord+LENGTH);
        }
        else {
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord+OFFSETS[offset], zCoord, xCoord+LENGTH, yCoord+OFFSETS[offset]+WIDTH, zCoord+LENGTH);
        }
    }

    public double[] getBlockBounds() {
        return bounds;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    //debug info
    @Override
    public void addDebugInfo(List<String> list) {
        list.add("GRATE:");
        super.addDebugInfo(list);
        list.add("Offset: "+offset);
        list.add("Orientation: " + orientation + " (" + (orientation==0?"xy":orientation==1?"zy":"xz") + ")");
        list.add("Bounds: ");
        list.add(" - x: " + bounds[0] + " - " + bounds[3]);
        list.add(" - y: " + bounds[1] + " - " + bounds[4]);
        list.add(" - z: " + bounds[2] + " - " + bounds[5]);
    }
}
