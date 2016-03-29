package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.storage.SeedStorageSlot;
import com.infinityraider.agricraft.tileentity.storage.TileEntitySeedStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTileEntitySeedStorage extends MessageAgriCraft {
    private BlockPos pos;
    private int slotId;
    private int amount;
    private int growth;
    private int gain;
    private int strength;

    @SuppressWarnings("unused")
    public MessageTileEntitySeedStorage() {}

    public MessageTileEntitySeedStorage(BlockPos pos, SeedStorageSlot slot) {
        this.pos = pos;
        if(slot!=null) {
            this.slotId = slot.getId();
            this.amount = slot.count;
            NBTTagCompound tag = slot.getTag();
            this.growth = tag.getInteger(AgriCraftNBT.GROWTH);
            this.gain = tag.getInteger(AgriCraftNBT.GAIN);
            this.strength = tag.getInteger(AgriCraftNBT.STRENGTH);
        }
        else {
            this.slotId = -1;
        }
    }

    private NBTTagCompound getTag() {
        NBTTagCompound tag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(tag, (short) growth, (short) gain, (short) strength, true);
        return tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
        this.slotId = buf.readInt();
        if(this.slotId>=0) {
            this.amount = buf.readInt();
            this.growth = buf.readInt();
            this.gain = buf.readInt();
            this.strength = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
        buf.writeInt(this.slotId);
        if(this.slotId>=0) {
            buf.writeInt(this.amount);
            buf.writeInt(this.growth);
            buf.writeInt(this.gain);
            buf.writeInt(this.strength);
        }
    }

    public static class MessageHandler implements IMessageHandler<MessageTileEntitySeedStorage, IMessage> {
        @Override
        public IMessage onMessage(MessageTileEntitySeedStorage message, MessageContext context) {
            TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.pos);
            if(te!=null && te instanceof TileEntitySeedStorage) {
                TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
                ItemStack stack = storage.getLockedSeed();
                stack.stackSize = message.amount;
                stack.setTagCompound(message.getTag());
                storage.setSlotContents(message.slotId, stack);
            }
            return null;
        }
    }
}
