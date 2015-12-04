package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public abstract class NetworkWrapperAgriCraft {
    public static final int messageContainerSeedStorage_ID = 0;
    public static final int messageTileEntitySeedStorage_ID = 1;
    public static final int messageSyncMutation_ID = 2;
    public static final int messageFertiliserApplied_ID = 3;
    public static final int messageGuiSeedStorageClearSeed_ID = 4;
    public static final int messageSyncFluidlevel_ID = 5;
    public static final int messagePeripheralNeighBourCheck_ID = 6;
    public static final int messageSendNEIsettings_ID = 7;

    public static SimpleNetworkWrapper wrapper;

    public static void init() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
        initMessages();
    }

    private static void initMessages() {
        wrapper.registerMessage(MessageContainerSeedStorage.MessageHandler.class, MessageContainerSeedStorage.class, messageContainerSeedStorage_ID, Side.SERVER);
        wrapper.registerMessage(MessageGuiSeedStorageClearSeed.MessageHandler.class, MessageGuiSeedStorageClearSeed.class, messageGuiSeedStorageClearSeed_ID, Side.SERVER);
        wrapper.registerMessage(MessageTileEntitySeedStorage.MessageHandler.class, MessageTileEntitySeedStorage.class, messageTileEntitySeedStorage_ID, Side.CLIENT);
        wrapper.registerMessage(MessageSyncMutation.MessageHandler.class, MessageSyncMutation.class, messageSyncMutation_ID, Side.CLIENT);
        wrapper.registerMessage(MessageFertiliserApplied.MessageHandler.class, MessageFertiliserApplied.class, messageFertiliserApplied_ID, Side.CLIENT);
        wrapper.registerMessage(MessageSyncFluidLevel.MessageHandler.class, MessageSyncFluidLevel.class, messageSyncFluidlevel_ID, Side.CLIENT);
        wrapper.registerMessage(MessagePeripheralCheckNeighbours.MessageHandler.class, MessagePeripheralCheckNeighbours.class, messagePeripheralNeighBourCheck_ID, Side.CLIENT);
        wrapper.registerMessage(MessageSendNEISetting.MessageHandler.class, MessageSendNEISetting.class, messageSendNEIsettings_ID, Side.CLIENT);
    }
}
