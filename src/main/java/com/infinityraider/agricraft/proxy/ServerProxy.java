package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.JsonSyncHandler;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

@OnlyIn(Dist.DEDICATED_SERVER)
@SuppressWarnings("unused")
public class ServerProxy implements IServerProxyBase<Config>, IProxy {


    @Override
    public void onDedicatedServerSetupEvent(final FMLDedicatedServerSetupEvent event) {
        PluginHandler.onServerSetup(event);
    }

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(JsonSyncHandler.getServerInstance());
    }
}
