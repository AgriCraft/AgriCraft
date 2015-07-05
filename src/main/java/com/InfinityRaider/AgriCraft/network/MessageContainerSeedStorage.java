package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageDummy;
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

    public MessageContainerSeedStorage() {}

    public MessageContainerSeedStorage(ItemStack stack, EntityPlayer player, int slotId) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.amount = stack.stackSize;
        this.player = player;
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
            if(container!=null && container instanceof ContainerSeedStorageDummy) {
                ContainerSeedStorageDummy storage = (ContainerSeedStorageDummy) container;
                storage.moveStackFromTileEntityToPlayer(message.slotId, new ItemStack(message.item, message.amount, message.meta));
            }
            return null;
        }
    }
}
