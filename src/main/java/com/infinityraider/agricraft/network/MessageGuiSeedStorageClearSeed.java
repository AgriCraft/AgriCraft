package com.infinityraider.agricraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.blocks.tiles.storage.ISeedStorageControllable;
import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

public class MessageGuiSeedStorageClearSeed extends MessageBase<IMessage> {
    private EntityPlayer player;

    @SuppressWarnings("unused")
    public MessageGuiSeedStorageClearSeed() {}

    public MessageGuiSeedStorageClearSeed(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        final Container container = this.player.openContainer;
        if(container instanceof ContainerSeedStorageBase) {
            final ContainerSeedStorageBase storage = ((ContainerSeedStorageBase) container);
            final TileEntity tileEntity = storage.getTileEntity();
            if(tileEntity instanceof ISeedStorageControllable) {
                ((ISeedStorageControllable) tileEntity).clearLockedSeed();
            }
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(buf, player);
    }
}
