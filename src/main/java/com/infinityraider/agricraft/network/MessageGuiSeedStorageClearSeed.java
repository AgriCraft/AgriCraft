package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.tiles.storage.ISeedStorageControllable;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageGuiSeedStorageClearSeed extends MessageBase {
    public MessageGuiSeedStorageClearSeed() {}

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        final Container container = ctx.getSender().openContainer;
        if (container instanceof ContainerSeedStorageBase) {
            final ContainerSeedStorageBase storage = ((ContainerSeedStorageBase) container);
            final TileEntity tileEntity = storage.getTile();
            if (tileEntity instanceof ISeedStorageControllable) {
                ((ISeedStorageControllable) tileEntity).clearLockedSeed();
            }
        }
    }
}
