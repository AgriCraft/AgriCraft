package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.reference.Reference;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapper {
    private static NetworkWrapper INSTANCE = new NetworkWrapper();

    public static NetworkWrapper getInstance() {
        return INSTANCE;
    }

    private final SimpleNetworkWrapper wrapper;
    private int nextId = 0;

    private NetworkWrapper() {
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID.toLowerCase());
    }

    public void initMessages() {
        this.registerMessage(MessageContainerSeedStorage.class);
        this.registerMessage(MessageGuiSeedStorageClearSeed.class);
        this.registerMessage(MessageTileEntitySeedStorage.class);
        this.registerMessage(MessageFertiliserApplied.class);
        this.registerMessage(MessageSyncFluidLevel.class);
        this.registerMessage(MessagePeripheralCheckNeighbours.class);
        this.registerMessage(MessageSendNEISetting.class);
		this.registerMessage(MessageSyncPlantJson.class);
		this.registerMessage(MessageSyncMutationJson.class);
    }

    public void sendToAll(MessageBase message) {
        wrapper.sendToAll(message);
    }

    public void sendTo(MessageBase message, EntityPlayerMP player) {
        wrapper.sendTo(message, player);
    }

    public void sendToAllAround(MessageBase message, World world, double x, double y, double z, double range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, range));
    }

    public void sendToAllAround(MessageBase message, int dimension, double x, double y, double z, double range) {
        sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    public void sendToAllAround(MessageBase message, NetworkRegistry.TargetPoint point) {
        wrapper.sendToAllAround(message, point);
    }

    public void sendToDimension(MessageBase messageBase, World world) {
        sendToDimension(messageBase, world.provider.getDimension());
    }

    public void sendToDimension(MessageBase message, int dimensionId) {
        wrapper.sendToDimension(message, dimensionId);
    }

    public void sendToServer(MessageBase message) {
        wrapper.sendToServer(message);
    }

    private <REQ extends MessageBase<REPLY>, REPLY extends IMessage> void registerMessage(Class<? extends REQ> message) {
        try {
            Side side = message.getDeclaredConstructor().newInstance().getMessageHandlerSide();
            wrapper.registerMessage(new MessageHandler<REQ, REPLY>(), message, nextId, side);
            nextId = nextId + 1;
        } catch (Exception e) {
            AgriCore.getLogger("AgriCraft").trace(e);
        }
    }
}
