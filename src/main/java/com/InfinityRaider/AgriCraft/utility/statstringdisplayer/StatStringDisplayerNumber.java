package com.InfinityRaider.AgriCraft.utility.statstringdisplayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatStringDisplayerNumber extends StatStringDisplayer {
    @Override
    public String getStatDisplayString(int stat, int max) {
        return ""+stat;
    }
}
