package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageContainerSeedStorage extends MessageBase {
    private Item item;
    private int amount;
    private int slotId;

    public MessageContainerSeedStorage() {}

    public MessageContainerSeedStorage(ItemStack stack, int slotId) {
        this();
        this.item = stack.getItem();
        this.amount = stack.getCount();
        this.slotId = slotId;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if (ctx.getSender().openContainer instanceof ContainerSeedStorageBase) {
            ContainerSeedStorageBase storage = (ContainerSeedStorageBase) ctx.getSender().openContainer;
            storage.moveStackFromTileEntityToPlayer(slotId, new ItemStack(item, amount));
        }

    }
}
