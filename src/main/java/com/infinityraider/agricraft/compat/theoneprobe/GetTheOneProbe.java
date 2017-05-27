/*
 */
package com.infinityraider.agricraft.compat.theoneprobe;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;

/**
 * Since TheOneProbe has a terrible registration interface.
 */
public class GetTheOneProbe implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe probe) {
        //All blocks.
        AgriCore.getLogger("agricraft").debug("One Probe Integration Enabled!");
        IProbeInfoProvider agriProvider = new AgriOneProbeAdapter();
        probe.registerProvider(agriProvider);
        return null;
    }

}
