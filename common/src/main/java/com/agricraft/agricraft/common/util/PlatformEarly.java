package com.agricraft.agricraft.common.util;

public abstract class PlatformEarly {
    public static PlatformEarly delegate = null;

    static {
        try {
            Class.forName("com.agricraft.agricraft.common.util.fabric.FabricPlatformEarly");
        } catch (Throwable ignored) {
        }
        try {
            Class.forName("com.agricraft.agricraft.common.util.forge.ForgePlatformEarly");
        } catch (Throwable ignored) {
        }
    }

    public static PlatformEarly get() {
        return PlatformEarly.delegate;
    }

    public abstract boolean isModLoaded(String id);
}
