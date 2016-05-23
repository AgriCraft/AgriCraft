package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.tileentity.storage.ISeedStorageControllable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageGuiSeedStorageClearSeed extends MessageBase {
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
        Container container = this.player.openContainer;
        if(container!=null && container instanceof ContainerSeedStorageBase) {
            ContainerSeedStorageBase storage = (ContainerSeedStorageBase) container;
            TileEntity tileEntity = storage.getTileEntity();
            if(tileEntity != null && (tileEntity instanceof ISeedStorageControllable)) {
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
        this.writePlayerToByteBuf(player, buf);
    }
}
