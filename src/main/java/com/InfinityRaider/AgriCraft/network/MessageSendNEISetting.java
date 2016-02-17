package com.infinityraider.agricraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSendNEISetting extends MessageAgriCraft {
    private String className;
    private boolean value;

    @SuppressWarnings("unused")
    public MessageSendNEISetting() {}

    public MessageSendNEISetting(String className, boolean active) {
        this.className = className;
        this.value = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        this.className = new String(buf.readBytes(length).array());
        this.value = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(className.length());
        buf.writeBytes(className.getBytes());
        buf.writeBoolean(value);
    }

    public static class MessageHandler implements IMessageHandler<MessageSendNEISetting, IMessage> {
        @Override
        public IMessage onMessage(MessageSendNEISetting message, MessageContext ctx) {
            //NEIHelper.setHandlerStatus(message.className, message.value);
            return null;
        }
    }
}
