package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Iterator;
import java.util.List;

public abstract class MessageAgriCraft implements IMessage {
    protected BlockPos readBlockPosFromByteBuf(ByteBuf buf) {
        return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    protected ByteBuf writeBlockPosToByteBuf(ByteBuf buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        return buf;
    }

    protected EntityPlayer getPlayerFromByteBuf(ByteBuf buf) {
        int playerNameLength = buf.readInt();
        String name = new String(buf.readBytes(playerNameLength).array());
        List list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        EntityPlayer player = null;
        Iterator iterator = list.iterator();
        while (iterator.hasNext() && player==null) {
            EntityPlayer nextPlayer = (EntityPlayer)iterator.next();
            if(nextPlayer.getGameProfile().getName().equals(name)) {
                player = nextPlayer;
            }
        }
        return player;
    }

    protected ByteBuf writePlayerToByteBuf(EntityPlayer player, ByteBuf buf) {
        String playerName = player==null?"null":player.getGameProfile().getName();
        buf.writeInt(playerName.length());
        buf.writeBytes(playerName.getBytes());
        return buf;
    }

    protected Item readItemFromByteBuf(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return  Item.itemRegistry.getObject(new ResourceLocation(itemName));
    }

    protected void writeItemToByteBuf(Item item, ByteBuf buf) {
        String itemName = item==null?"null":Item.itemRegistry.getNameForObject(item).toString();
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
    }

    protected ItemStack readItemStackToByteBuf(ByteBuf buf) {
        Item item = this.readItemFromByteBuf(buf);
        int meta = buf.readInt();
        int amount = buf.readInt();
        return item==null?null:new ItemStack(item, amount, meta);
    }

    protected ByteBuf writeItemStackFromByteBuf(ByteBuf buf, ItemStack stack) {
        this.writeItemToByteBuf(stack.getItem(), buf);
        buf.writeInt(stack.getItemDamage());
        buf.writeInt(stack.stackSize);
        return buf;
    }

    protected Entity readEntityFromByteBuf(ByteBuf buf) {
        return AgriCraft.proxy.getEntityById(buf.readInt(), buf.readInt());
    }

    protected ByteBuf writeEntityToByteBuf(ByteBuf buf, Entity e) {
        buf.writeInt(e.worldObj.provider.getDimensionId());
        buf.writeInt(e.getEntityId());
        return buf;
    }
}
