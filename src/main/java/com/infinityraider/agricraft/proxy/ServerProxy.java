package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.handler.PlayerConnectToServerHandler;
import com.infinityraider.infinitylib.proxy.base.IServerProxyBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class ServerProxy implements IServerProxyBase<Config>, IProxy {

    @Override
    public void registerEventHandlers() {
        IProxy.super.registerEventHandlers();
        this.registerEventHandler(PlayerConnectToServerHandler.getInstance());
    }
}
