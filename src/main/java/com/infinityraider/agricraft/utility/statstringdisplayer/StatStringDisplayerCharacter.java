package com.infinityraider.agricraft.utility.statstringdisplayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatStringDisplayerCharacter extends StatStringDisplayer {
    private final char c;

    public StatStringDisplayerCharacter(char c) {
        this.c = c;
    }

    @Override
    public String getStatDisplayString(int stat, int max) {
        StringBuilder buff = new StringBuilder();
        while(stat>0) {
            buff.append(c);
            stat--;
        }
        return buff.toString();
    }
}
