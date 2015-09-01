package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.api.v1.IFertilizer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MessageFertilizerApplied extends MessageAgriCraft {
    private int x;
    private int y;
    private int z;
    private Item fertiliser;
    private int meta;

    public MessageFertilizerApplied() {

    }

    public MessageFertilizerApplied(ItemStack fertiliser, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.fertiliser = fertiliser.getItem();
        this.meta = fertiliser.getItemDamage();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.fertiliser = this.readItemFromByteBuf(buf);
        this.meta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        this.writeItemToByteBuf(this.fertiliser, buf);
        buf.writeInt(this.meta);
    }

    public static class MessageHandler implements IMessageHandler<MessageFertilizerApplied, IMessage> {
        @Override
        public IMessage onMessage(MessageFertilizerApplied message, MessageContext ctx) {
            if(message!=null && message.fertiliser!=null && message.fertiliser instanceof IFertilizer) {
                ((IFertilizer) message.fertiliser).performClientAnimations(message.meta, Minecraft.getMinecraft().thePlayer.worldObj, message.x, message.y, message.z);
            }
            return null;
        }
    }
}
