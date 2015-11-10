package com.InfinityRaider.AgriCraft.utility.statstringdisplayer;

import com.InfinityRaider.AgriCraft.api.v2.IStatStringDisplayer;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class StatStringDisplayer implements IStatStringDisplayer {
    private static IStatStringDisplayer INSTANCE;

    public static IStatStringDisplayer instance() {
        if(INSTANCE == null) {
            INSTANCE = getInstance(ConfigurationHandler.statDisplay);
        }
        return INSTANCE;
    }

    public static void setStatStringDisplayer(IStatStringDisplayer displayer) {
        INSTANCE = displayer;
    }

    private static IStatStringDisplayer getInstance(String config) {
        if(config.equals("NUMBER")) {
            return new StatStringDisplayerNumber();
        }
        if(config.equals("FRACTION")) {
            return new StatStringDisplayerFraction();
        }
        String[] splitConfig = config.split("-");
        if(splitConfig.length>1) {
            if (splitConfig[0].equals("CHARACTER")){
                return new StatStringDisplayerCharacter(splitConfig[1].charAt(0));
            }
            if(splitConfig[0].equals("KEYWORD")) {
                String keyword = splitConfig.length>2?splitConfig[splitConfig.length-1]:splitConfig[1];
                IStatStringDisplayer displayer = splitConfig.length>2?getInstance(concatenateBackwards(splitConfig)):new StatStringDisplayerNumber();
                return new StatStringDisplayerKeyword(displayer, keyword);
            }
        }
        return new StatStringDisplayerNumber();
    }

    private static String concatenateBackwards(String[] splitString) {
        StringBuilder builder = new StringBuilder();
        for(int i = 1;i<splitString.length-1;i++) {
            builder.append(splitString[i]);
            if(i<splitString.length-2) {
                builder.append("-");
            }
        }
        return builder.toString();
    }
}
