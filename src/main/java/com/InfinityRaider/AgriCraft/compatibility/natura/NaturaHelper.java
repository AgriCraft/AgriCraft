package com.InfinityRaider.AgriCraft.compatibility.natura;


import com.InfinityRaider.AgriCraft.utility.LogHelper;

public class NaturaHelper {
    public static int getTextureIndex(int plantMeta, int seedMeta) {
        //barley: seedMeta = 0
        //cotton: seedMeta = 1
        switch (plantMeta) {
            case 0: return seedMeta*4;
            case 1: return seedMeta*4;
            case 2: return seedMeta*5;
            case 3: return 1+seedMeta*4;
            case 4: return 1+seedMeta*5;
            case 5: return 2+seedMeta*4;
            case 6: return 2+seedMeta*5;
            case 7: return 3+seedMeta*5;
        }
        LogHelper.info("Invalid Metadata on NaturaCrop");   //this should never happen
        return 0;
    }


}
