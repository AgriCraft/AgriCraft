package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageContainerSeedStorage extends MessageBase<IMessage> {

    private Item item;
    private int meta;
    private int amount;
    private EntityPlayer player;
    private int slotId;

    public MessageContainerSeedStorage() {
    }

    public MessageContainerSeedStorage(ItemStack stack, int slotId) {
        this();
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.amount = stack.getCount();
        this.player = AgriCraft.proxy.getClientPlayer();
        this.slotId = slotId;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if (player.openContainer instanceof ContainerSeedStorageBase) {
            ContainerSeedStorageBase storage = (ContainerSeedStorageBase) player.openContainer;
            storage.moveStackFromTileEntityToPlayer(slotId, new ItemStack(item, amount, meta));
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }
}
