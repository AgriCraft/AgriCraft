package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGrate extends TileEntityCustomWood {
    private short orientation;  //0: xy, 1: zy, 2: xz
    private short offset;
    private short vines; //0: no, 1: front, 2: back, 3: both

    public TileEntityGrate() {
        super();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setShort(Names.NBT.flag, orientation);
        tag.setShort(Names.NBT.meta, offset);
        tag.setShort(Names.NBT.weed, vines);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.orientation = tag.getShort(Names.NBT.flag);
        this.offset = tag.getShort(Names.NBT.meta);
        this.vines = tag.getShort(Names.NBT.weed);
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

    public short getOrientationValue() {
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

    public short getOffset() {
        return offset;
    }

    public boolean isPlayerInFront(EntityPlayer player) {
        double offset = (this.offset*7.000D)/16.000D;
        if(this.orientation == 0) {
            return player.posZ < this.zCoord + offset + Constants.UNIT;
        }
        else if(this.orientation == 1) {
            return player.posX < this.xCoord + offset + Constants.UNIT;
        }
        else {
            return player.posY < this.yCoord + offset + Constants.UNIT;
        }
    }

    public boolean hasVines(boolean front) {
        return vines==3 || (front?vines==1:vines==2);
    }

    public boolean addVines(boolean front) {
        if(hasVines(front)) {
            return false;
        } else {
            this.vines = (short) (vines+(front?1:2));
            this.markForUpdate();
            return true;
        }
    }

    public boolean removeVines(boolean front) {
        if(hasVines(front)) {
            this.vines = (short) Math.max((vines - (front ? 1 : 2)), 0);
            this.markForUpdate();
            return true;
        }
        return false;
    }

    public AxisAlignedBB getBoundingBox() {
        double offset = (this.offset*7.000D)/16.000D;
        if(this.orientation == 0) {
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord+offset, xCoord+1, yCoord+1, zCoord+offset+2*Constants.UNIT);
        }
        else if(this.orientation == 1) {
            return AxisAlignedBB.getBoundingBox(xCoord+offset, yCoord, zCoord, xCoord+offset+2*Constants.UNIT, yCoord+1, zCoord+1);
        }
        else {
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord+offset, zCoord, xCoord+1, yCoord+offset+2*Constants.UNIT, zCoord+1);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}
