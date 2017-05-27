package com.infinityraider.agricraft.compat.theoneprobe;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.AgriPlugin;
import com.infinityraider.agricraft.api.IAgriPlugin;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@AgriPlugin
public class TheOneProbePlugin implements IAgriPlugin {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void initPlugin() {
        AgriCore.getLogger("agricraft").debug("Calling One Probe Register! Result: {0}",
                FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", this.getClass().getPackage().getName() + ".GetTheOneProbe"));
    }

}
