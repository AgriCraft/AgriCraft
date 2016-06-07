package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageTileEntitySeedStorage extends MessageBase {
    private BlockPos pos;
    private int slotId;
	private int amount;
    private PlantStats stats;

    @SuppressWarnings("unused")
    public MessageTileEntitySeedStorage() {}

    public MessageTileEntitySeedStorage(BlockPos pos, SeedStorageSlot slot) {
        this.pos = pos;
        if(slot!=null) {
            this.slotId = slot.getId();
            this.amount = slot.count;
			this.stats = new PlantStats(slot.getTag());
        }
        else {
            this.slotId = -1;
        }
    }

    @Override
    public Side getMessageHandlerSide() {
        return Side.CLIENT;
    }

    @Override
    protected void processMessage(MessageContext ctx) {
        TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(this.pos);
        if(te != null && te instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
            ItemStack stack = storage.getLockedSeed();
            stack.stackSize = this.amount;
			NBTTagCompound tag = new NBTTagCompound();
			this.stats.writeToNBT(tag);
            stack.setTagCompound(tag);
            storage.setSlotContents(this.slotId, stack);
        }
    }

    @Override
    protected IMessage getReply(MessageContext ctx) {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = readBlockPosFromByteBuf(buf);
        this.slotId = buf.readInt();
        if(this.slotId>=0) {
            this.amount = buf.readInt();
			this.stats = new PlantStats(buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.writeBlockPosToByteBuf(buf, pos);
        buf.writeInt(this.slotId);
        if(this.slotId>=0) {
            buf.writeInt(this.amount);
            buf.writeInt(this.stats.getStatCode());
        }
    }
}
