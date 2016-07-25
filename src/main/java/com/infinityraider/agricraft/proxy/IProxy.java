package com.infinityraider.agricraft.proxy;

import com.infinityraider.infinitylib.proxy.IProxyBase;

public interface IProxy extends IProxyBase {
    /** Registers a villager skin on the client, does nothing on the server */
    void registerVillagerSkin(int id, String resource);
}
