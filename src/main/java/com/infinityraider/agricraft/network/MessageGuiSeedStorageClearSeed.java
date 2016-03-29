package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.agricraft.tileentity.storage.ISeedStorageControllable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGuiSeedStorageClearSeed extends MessageAgriCraft {
    private EntityPlayer player;

    @SuppressWarnings("unused")
    public MessageGuiSeedStorageClearSeed() {}

    public MessageGuiSeedStorageClearSeed(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.readPlayerFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writePlayerToByteBuf(player, buf);
    }

    public static class MessageHandler implements IMessageHandler<MessageGuiSeedStorageClearSeed, IMessage> {
        @Override
        public IMessage onMessage(MessageGuiSeedStorageClearSeed message, MessageContext context) {
            Container container = message.player.openContainer;
            if(container!=null && container instanceof ContainerSeedStorageBase) {
                ContainerSeedStorageBase storage = (ContainerSeedStorageBase) container;
                TileEntity tileEntity = storage.getTileEntity();
                if(tileEntity != null && (tileEntity instanceof ISeedStorageControllable)) {
                    ((ISeedStorageControllable) tileEntity).clearLockedSeed();
                }
            }
            return null;
        }
    }
}
