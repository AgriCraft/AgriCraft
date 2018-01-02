package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageFertilizerApplied extends MessageBase<IMessage> {

    private BlockPos pos;
    private Item fertilizer;
    private int meta;

    public MessageFertilizerApplied() {
    }

    public MessageFertilizerApplied(ItemStack fertilizer, BlockPos pos) {
        this();
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
        if (this.fertilizer instanceof IAgriFertilizer) {
            ((IAgriFertilizer) this.fertilizer)
                    .performClientAnimations(this.meta, Minecraft.getMinecraft().player.getEntityWorld(), this.pos);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
