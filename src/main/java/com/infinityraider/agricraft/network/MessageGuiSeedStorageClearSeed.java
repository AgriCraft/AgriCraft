package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.tiles.storage.ISeedStorageControllable;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageGuiSeedStorageClearSeed extends MessageBase<IMessage> {

    private EntityPlayer player;

    public MessageGuiSeedStorageClearSeed() {
    }

    public MessageGuiSeedStorageClearSeed(EntityPlayer player) {
        this();
        this.player = player;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        final Container container = this.player.openContainer;
        if (container instanceof ContainerSeedStorageBase) {
            final ContainerSeedStorageBase storage = ((ContainerSeedStorageBase) container);
            final TileEntity tileEntity = storage.getTile();
            if (tileEntity instanceof ISeedStorageControllable) {
                ((ISeedStorageControllable) tileEntity).clearLockedSeed();
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
