package com.infinityraider.agricraft.plugins.bloodmagic;

import wayoftime.bloodmagic.ritual.harvest.HarvestRegistry;

public class BloodMagicCompat {
    static void execute() {
        HarvestRegistry.registerHandler(new AgriHarvestHandler());
    }
}
