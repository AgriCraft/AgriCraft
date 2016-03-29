package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.api.v1.IFertiliser;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFertiliserApplied extends MessageAgriCraft {
    private BlockPos pos;
    private Item fertiliser;
    private int meta;

    @SuppressWarnings("unused")
    public MessageFertiliserApplied() {}

    public MessageFertiliserApplied(ItemStack fertiliser, BlockPos pos) {
        this.pos = pos;
        this.fertiliser = fertiliser.getItem();
        this.meta = fertiliser.getItemDamage();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
        this.fertiliser = this.readItemFromByteBuf(buf);
        this.meta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
        this.writeItemToByteBuf(this.fertiliser, buf);
        buf.writeInt(this.meta);
    }

    public static class MessageHandler implements IMessageHandler<MessageFertiliserApplied, IMessage> {
        @Override
        public IMessage onMessage(MessageFertiliserApplied message, MessageContext ctx) {
            if(message!=null && message.fertiliser!=null && message.fertiliser instanceof IFertiliser) {
                ((IFertiliser) message.fertiliser).performClientAnimations(message.meta, Minecraft.getMinecraft().thePlayer.worldObj, message.pos);
            }
            return null;
        }
    }
}
