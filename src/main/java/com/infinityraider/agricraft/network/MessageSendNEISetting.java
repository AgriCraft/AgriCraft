package com.infinityraider.agricraft.network;

import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSendNEISetting extends MessageBase<IMessage> {
    private String className;
    private boolean value;

    @SuppressWarnings("unused")
    public MessageSendNEISetting() {}

    public MessageSendNEISetting(String className, boolean active) {
        this.className = className;
        this.value = active;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        //NEIHelper.setHandlerStatus(message.className, message.value);
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
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
}
