package com.infinityraider.agricraft.plugins.theoneprobe;

import java.util.function.Supplier;

public class TheOneProbeSupplier {
    public static Supplier<?> getSupplier() {
        return TheOneProbeCompat::getInstance;
    }
}
