package com.infinityraider.agricraft.network.json;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

public abstract class MessageSyncElement<T> extends MessageBase {

    protected T element;
    protected int index;
    protected int count;

    public MessageSyncElement() {
    }

    public MessageSyncElement(T element, int index, int count) {
        this.element = element;
        this.index = index;
        this.count = count;
    }

    @Override
    public final Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected final void processMessage(MessageContext ctx) {
        AgriCore.getLogger("Agri-Net").info("Recieved Element {0} ({1} of {2}).", element.getClass().getCanonicalName(), index + 1, count);
        if (this.index == 0) {
            onSyncStart(ctx);
        }
        onMessage(ctx);
        if (this.index == this.count - 1) {
            onFinishSync(ctx);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public final void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
        this.count = buf.readInt();
        this.element = fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public final void toBytes(ByteBuf buf) {
        buf.writeInt(index);
        buf.writeInt(count);
        ByteBufUtils.writeUTF8String(buf, toString(element));
    }

    @SideOnly(Side.CLIENT)
    public final String getServerId() {
        final ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        return "server_" + data.serverIP.replaceAll("\\.", "-").replaceAll(":", "_");
    }

    protected abstract void onSyncStart(MessageContext ctx);

    protected abstract void onMessage(MessageContext ctx);

    protected abstract void onFinishSync(MessageContext ctx);

    protected abstract String toString(T element);

    protected abstract T fromString(String element);

}
