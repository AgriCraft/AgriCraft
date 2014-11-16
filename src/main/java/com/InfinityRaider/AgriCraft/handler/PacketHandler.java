package com.InfinityRaider.AgriCraft.handler;

//Cheers to Pahimar, the code here is heavily based off of his (I've learnt how to handle and send packets using his EE3 Code)

import com.InfinityRaider.AgriCraft.network.MessageTileEntityCrop;
import com.InfinityRaider.AgriCraft.network.MessageTileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.network.MessageTileEntityTank;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());

    public static void init() {
        instance.registerMessage(MessageTileEntityCrop.class, MessageTileEntityCrop.class, 0, Side.CLIENT);
        instance.registerMessage(MessageTileEntitySeedAnalyzer.class, MessageTileEntitySeedAnalyzer.class, 1, Side.CLIENT);
        instance.registerMessage(MessageTileEntityTank.class, MessageTileEntityTank.class, 2, Side.CLIENT);
        LogHelper.info("Packet handler registered");
    }
}