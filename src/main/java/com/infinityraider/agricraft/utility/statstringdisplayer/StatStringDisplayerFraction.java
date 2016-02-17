package com.infinityraider.agricraft.utility.statstringdisplayer;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatStringDisplayerFraction extends StatStringDisplayer {
    @Override
    public String getStatDisplayString(int stat, int max) {
        return stat+"/"+max;
    }
}
