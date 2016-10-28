package com.infinityraider.agricraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

public class MessageFertilizerApplied extends MessageBase<IMessage> {
    private BlockPos pos;
    private Item fertilizer;
    private int meta;

    @SuppressWarnings("unused")
    public MessageFertilizerApplied() {}

    public MessageFertilizerApplied(ItemStack fertilizer, BlockPos pos) {
        this.pos = pos;
        this.fertilizer = fertilizer.getItem();
        this.meta = fertilizer.getItemDamage();
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(this.fertilizer instanceof IAgriFertilizer) {
            ((IAgriFertilizer) this.fertilizer)
                    .performClientAnimations(this.meta, Minecraft.getMinecraft().thePlayer.worldObj, this.pos);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
        this.fertilizer = this.readItemFromByteBuf(buf);
        this.meta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
        this.writeItemToByteBuf(this.fertilizer, buf);
        buf.writeInt(this.meta);
    }
}
