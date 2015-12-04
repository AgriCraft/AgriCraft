package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MessageContainerSeedStorage extends MessageAgriCraft {
    private Item item;
    private int meta;
    private int amount;
    private EntityPlayer player;
    private int slotId;

    @SuppressWarnings("unused")
    public MessageContainerSeedStorage() {}

    public MessageContainerSeedStorage(ItemStack stack, int slotId) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.amount = stack.stackSize;
        this.player = AgriCraft.proxy.getClientPlayer();
        this.slotId = slotId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.item = this.readItemFromByteBuf(buf);
        this.meta = buf.readInt();
        this.amount = buf.readInt();
        this.player = this.getPlayerFromByteBuf(buf);
        this.slotId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeItemToByteBuf(this.item, buf);
        buf.writeInt(this.meta);
        buf.writeInt(this.amount);
        this.writePlayerToByteBuf(this.player, buf);
        buf.writeInt(this.slotId);
    }

    public static class MessageHandler implements IMessageHandler<MessageContainerSeedStorage, IMessage> {
        @Override
        public IMessage onMessage(MessageContainerSeedStorage message, MessageContext context) {
            Container container = message.player.openContainer;
            if(container!=null && container instanceof ContainerSeedStorageBase) {
                ContainerSeedStorageBase storage = (ContainerSeedStorageBase) container;
                storage.moveStackFromTileEntityToPlayer(message.slotId, new ItemStack(message.item, message.amount, message.meta));
            }
            return null;
        }
    }
}
