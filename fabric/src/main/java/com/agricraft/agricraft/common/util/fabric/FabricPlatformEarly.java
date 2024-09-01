package com.agricraft.agricraft.common.util.fabric;

import com.agricraft.agricraft.common.util.PlatformEarly;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformEarly extends PlatformEarly {
    static {
        PlatformEarly.delegate = new FabricPlatformEarly();
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
