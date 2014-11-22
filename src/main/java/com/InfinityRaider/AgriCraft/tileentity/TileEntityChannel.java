package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Constants;

public class TileEntityChannel extends TileEntityAgricraft {
    private int lvl;
    public static final int maxlvl = Constants.mB;

    public int getFluidLevel() {return this.lvl;}

    public void setFluidLevel(int lvl) {this.lvl=lvl;}
}
