package com.InfinityRaider.AgriCraft.utility.statstringdisplayer;

public class StatStringDisplayerFraction extends StatStringDisplayer {
    @Override
    public String getStatDisplayString(int stat, int max) {
        return stat+"/"+max;
    }
}
