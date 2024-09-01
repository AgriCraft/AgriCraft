package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.common.util.PlatformEarly;
import net.minecraftforge.fml.ModList;

public class ForgePlatformEarly extends PlatformEarly {
    static {
        PlatformEarly.delegate = new ForgePlatformEarly();
    }

    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }
}
