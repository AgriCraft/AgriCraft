package com.InfinityRaider.AgriCraft.utility.statstringdisplayer;

import com.InfinityRaider.AgriCraft.api.v2.IStatStringDisplayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StatStringDisplayerKeyword extends StatStringDisplayer {
    private final IStatStringDisplayer displayer;
    private final String keyword;

    public StatStringDisplayerKeyword(IStatStringDisplayer displayer, String keyword) {
        this.displayer = displayer;
        this.keyword = keyword;
    }

    @Override
    public String getStatDisplayString(int stat, int max) {
        return keyword+": "+displayer.getStatDisplayString(stat, max);
    }
}
