package com.infinityraider.agricraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import com.infinityraider.agricraft.api.v3.fertiliser.IAgriFertiliser;

public class MessageFertiliserApplied extends MessageBase {
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
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(this.fertiliser!=null && this.fertiliser instanceof IAgriFertiliser) {
            ((IAgriFertiliser) this.fertiliser).performClientAnimations(this.meta, Minecraft.getMinecraft().thePlayer.worldObj, this.pos);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
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
}
