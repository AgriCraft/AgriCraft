package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

    protected EntityPlayer readPlayerFromByteBuf(ByteBuf buf) {
        Entity entity = readEntityFromByteBuf(buf);
        return (entity instanceof EntityPlayer)?(EntityPlayer) entity:null;
    }

    protected ByteBuf writePlayerToByteBuf(EntityPlayer player, ByteBuf buf) {
        return writeEntityToByteBuf(buf, player);
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
        int id = buf.readInt();
        if(id < 0) {
            return null;
        }
        int dimension = buf.readInt();
        return AgriCraft.proxy.getEntityById(dimension, id);
    }

    protected ByteBuf writeEntityToByteBuf(ByteBuf buf, Entity e) {
        if (e == null) {
            buf.writeInt(-1);
            buf.writeInt(0);
        } else {
            buf.writeInt(e.getEntityId());
            buf.writeInt(e.worldObj.provider.getDimension());
        }
        return buf;
    }
}
