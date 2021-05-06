package com.infinityraider.agricraft.plugins.theoneprobe;

import mcjty.theoneprobe.api.*;

import java.util.function.Function;

public class TheOneProbeCompat implements Function<ITheOneProbe, Void> {
    private static final TheOneProbeCompat INSTANCE = new TheOneProbeCompat();

    public static TheOneProbeCompat getInstance() {
        return INSTANCE;
    }

    private TheOneProbeCompat() {}

    @Override
    public Void apply(ITheOneProbe probe) {
        probe.registerProvider(new AgriProbeBlockInfoProvider());
        probe.registerBlockDisplayOverride(new AgriProbeBlockDisplayOverride());
        return null;
    }
}
