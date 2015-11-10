package com.InfinityRaider.AgriCraft.utility.statstringdisplayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatStringDisplayerNumber extends StatStringDisplayer {
    @Override
    public String getStatDisplayString(int stat, int max) {
        return ""+stat;
    }
}
