package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageBase;
import com.InfinityRaider.AgriCraft.tileentity.storage.ISeedStorageControllable;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class MessageGuiSeedStorageClearSeed extends MessageAgriCraft {
    private EntityPlayer player;

    @SuppressWarnings("unused")
    public MessageGuiSeedStorageClearSeed() {}

    public MessageGuiSeedStorageClearSeed(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.player = this.getPlayerFromByteBuf(buf);
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
