package com.infinityraider.agricraft.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.container.ContainerSeedStorageBase;
import com.infinityraider.infinitylib.network.MessageBase;
import io.netty.buffer.ByteBuf;

public class MessageContainerSeedStorage extends MessageBase<IMessage> {
    private Item item;
    private int meta;
    private int amount;
    private EntityPlayer player;
    private int slotId;

    @SuppressWarnings("unused")
    public MessageContainerSeedStorage() {}

    public MessageContainerSeedStorage(ItemStack stack, int slotId) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.amount = stack.stackSize;
        this.player = AgriCraft.proxy.getClientPlayer();
        this.slotId = slotId;
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.SERVER;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        if(player.openContainer instanceof ContainerSeedStorageBase) {
            ContainerSeedStorageBase storage = (ContainerSeedStorageBase) player.openContainer;
            storage.moveStackFromTileEntityToPlayer(slotId, new ItemStack(item, amount, meta));
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.item = this.readItemFromByteBuf(buf);
        this.meta = buf.readInt();
        this.amount = buf.readInt();
        this.player = this.readPlayerFromByteBuf(buf);
        this.slotId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeItemToByteBuf(this.item, buf);
        buf.writeInt(this.meta);
        buf.writeInt(this.amount);
        this.writePlayerToByteBuf(buf, this.player);
        buf.writeInt(this.slotId);
    }
}
