package com.infinityraider.agricraft.plugins.cyclic;

import com.lothrazar.cyclic.api.IHarvesterOverride;

public class CyclicCompat {
    public static void registerHarvesterOverride() {
        IHarvesterOverride.registerHarvestOverrider(new AgriHarvesterOverride());
    }
}
