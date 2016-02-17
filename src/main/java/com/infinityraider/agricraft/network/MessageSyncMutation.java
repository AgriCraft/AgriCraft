package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncMutation extends MessageAgriCraft {
    private ItemStack parent1;
    private ItemStack parent2;
    private ItemStack result;
    private double chance;
    private boolean last;

    @SuppressWarnings("unused")
    public MessageSyncMutation() {
    }

    public MessageSyncMutation(Mutation mutation, boolean last) {
        this.parent1 = mutation.getParents()[0];
        this.parent2 = mutation.getParents()[1];
        this.result = mutation.getResult();
        this.chance = mutation.getChance();
        this.last = last;
    }

    private Mutation getMutation() {
        return new Mutation(result, parent1, parent2, (int) (100*chance));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.parent1 = readItemStackToByteBuf(buf);
        this.parent2 = readItemStackToByteBuf(buf);
        this.result = readItemStackToByteBuf(buf);
        this.chance = buf.readDouble();
        this.last = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeItemStackFromByteBuf(buf, parent1);
        writeItemStackFromByteBuf(buf, parent2);
        writeItemStackFromByteBuf(buf, result);
        buf.writeDouble(this.chance);
        buf.writeBoolean(this.last);
    }

    public static class MessageHandler implements IMessageHandler<MessageSyncMutation, IMessage> {
        @Override
        public IMessage onMessage(MessageSyncMutation message, MessageContext ctx) {
            MutationHandler.syncFromServer(message.getMutation(), message.last);
            return null;
        }
    }

}
