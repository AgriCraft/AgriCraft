package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public abstract class NetworkWrapperAgriCraft {
    public static final int messageContainerSeedStorage_ID = 0;

    public static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
        initMessages();
    }

    private static void initMessages() {
        wrapper.registerMessage(MessageContainerSeedStorage.MessageHandler.class, MessageContainerSeedStorage.class, messageContainerSeedStorage_ID, Side.SERVER);
    }
}
