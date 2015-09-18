package com.InfinityRaider.AgriCraft.network;

import io.netty.buffer.ByteBuf;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import com.InfinityRaider.AgriCraft.AgriCraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

public abstract class MessageAgriCraft implements IMessage {
    protected EntityPlayer getPlayerFromByteBuf(ByteBuf buf) {
        int playerNameLength = buf.readInt();
        String name = new String(buf.readBytes(playerNameLength).array());
        List list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        EntityPlayer player = null;
        Iterator iterator = list.iterator();
        while (iterator.hasNext() && player==null) {
            EntityPlayer nextPlayer = (EntityPlayer)iterator.next();
            if(nextPlayer.getDisplayName().equals(name)) {
                player = nextPlayer;
            }
        }
        return player;
    }

    protected void writePlayerToByteBuf(EntityPlayer player, ByteBuf buf) {
        String playerName = player==null?"null":player.getDisplayName();
        buf.writeInt(playerName.length());
        buf.writeBytes(playerName.getBytes());
    }

    protected Item readItemFromByteBuf(ByteBuf buf) {
        int itemNameLength = buf.readInt();
        String itemName = new String(buf.readBytes(itemNameLength).array());
        return  (Item) Item.itemRegistry.getObject(itemName);
    }

    protected void writeItemToByteBuf(Item item, ByteBuf buf) {
        String itemName = item==null?"null":Item.itemRegistry.getNameForObject(item);
        buf.writeInt(itemName.length());
        buf.writeBytes(itemName.getBytes());
    }

    protected ItemStack readItemStackToByteBuf(ByteBuf buf) {
        Item item = this.readItemFromByteBuf(buf);
        int meta = buf.readInt();
        int amount = buf.readInt();
        return item==null?null:new ItemStack(item, amount, meta);
    }

    protected void writeItemStackFromByteBuf(ByteBuf buf, ItemStack stack) {
        this.writeItemToByteBuf(stack.getItem(), buf);
        buf.writeInt(stack.getItemDamage());
        buf.writeInt(stack.stackSize);
    }

    protected Entity readEntityFromByteBuf(ByteBuf buf) {
        return AgriCraft.proxy.getEntityById(buf.readInt(), buf.readInt());
    }

    protected void writeEntityToByteBuf(ByteBuf buf, Entity e) {
        buf.writeInt(e.worldObj.provider.dimensionId);
        buf.writeInt(e.getEntityId());
    }
}
