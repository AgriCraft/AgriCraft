package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGrate extends TileEntityCustomWood {
	
	//Short is pointless here as java pretty much always upconverts to int during operations. Only good in arrays and serialization.
    private int orientation;  //0: xy, 1: zy, 2: xz
    private int offset;
    private int vines; //0: no, 1: front, 2: back, 3: both
    
    // This does not change. Why calculate every time?
    private static final double[] OFFSETS = new double[]{ // The compiler does these calculations at compile time so no need to worry.
    	0 * 7 * Constants.UNIT, //offset 0
    	1 * 7 * Constants.UNIT, //offset 1
    	2 * 7 * Constants.UNIT, //offset 2
    };
    
    private static final double WIDTH = 2 * Constants.UNIT;
    private static final double HEIGHT = 1;
    private static final double LENGTH = 1;

    public TileEntityGrate() {
        super();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setShort(Names.NBT.flag, (short)orientation);
        tag.setShort(Names.NBT.meta, (short)offset);
        tag.setShort(Names.NBT.weed, (short)vines);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.orientation = tag.getShort(Names.NBT.flag);
        this.offset = tag.getShort(Names.NBT.meta);
        this.vines = tag.getShort(Names.NBT.weed);
    }

    public void setOrientationValue(int value) {
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

    public void setOffSet(int value) {
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
        switch(this.orientation) {
        	case 0: //x
        		return AxisAlignedBB.getBoundingBox(this.xCoord + OFFSETS[offset], this.yCoord, this.zCoord, this.xCoord + OFFSETS[offset] + WIDTH, this.yCoord + LENGTH, this.zCoord + HEIGHT);
        	case 1: //z
        		return AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord + OFFSETS[offset], this.xCoord + LENGTH, this.yCoord + HEIGHT, this.zCoord + OFFSETS[offset] + WIDTH);
        	case 2: //y
        	default: //or something is wrong.
        		return AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord + OFFSETS[offset], this.zCoord, this.xCoord + LENGTH, this.yCoord + OFFSETS[offset] + WIDTH, this.zCoord + HEIGHT);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }
}
