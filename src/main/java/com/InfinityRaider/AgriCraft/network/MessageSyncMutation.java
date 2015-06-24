package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MessageSyncMutation implements IMessage {
    private ItemStack parent1;
    private ItemStack parent2;
    private ItemStack result;
    private double chance;
    private boolean last;

    public MessageSyncMutation() {
    }

    public MessageSyncMutation(Mutation mutation, boolean last) {
        this.parent1 = mutation.parent1;
        this.parent2 = mutation.parent2;
        this.result = mutation.result;
        this.chance = mutation.chance;
        this.last = last;
    }

    private Mutation getMutation() {
        return new Mutation(result, parent1, parent2, (int) (100*chance));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.parent1 = readItemStack(buf);
        this.parent2 = readItemStack(buf);
        this.result = readItemStack(buf);
        this.chance = buf.readDouble();
        this.last = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeItemStack(buf, parent1);
        writeItemStack(buf, parent2);
        writeItemStack(buf, result);
        buf.writeDouble(this.chance);
        buf.writeBoolean(this.last);
    }

    private ItemStack readItemStack(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        Item item = (Item) Item.itemRegistry.getObject(itemName);
        int meta = buf.readInt();
        return item==null?null:new ItemStack(item, 1, meta);
    }

    private void writeItemStack(ByteBuf buf, ItemStack stack) {
        String itemName = stack.getItem()==null?"null":Item.itemRegistry.getNameForObject(stack.getItem());
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
        buf.writeInt(stack.getItemDamage());
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncMutation, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncMutation message, MessageContext ctx) {
            MutationHandler.syncFromServer(message.getMutation(), message.last);
            return null;
        }
    }

}
