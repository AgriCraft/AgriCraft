package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageFertilizerApplied extends MessageBase {
    private BlockPos pos;
    private ItemStack fertilizer;

    public MessageFertilizerApplied() {}

    public MessageFertilizerApplied(ItemStack fertilizer, BlockPos pos) {
        this();
        this.pos = pos;
        this.fertilizer = fertilizer;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if (this.fertilizer.getItem() instanceof IAgriFertilizer) {
            ((IAgriFertilizer) this.fertilizer.getItem()).performClientAnimations(AgriCraft.instance.getClientWorld(), this.pos, this.fertilizer);
        }
    }
}
