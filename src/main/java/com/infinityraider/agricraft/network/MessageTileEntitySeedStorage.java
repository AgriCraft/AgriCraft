package com.infinityraider.agricraft.network;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.tiles.storage.SeedStorageSlot;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import com.infinityraider.infinitylib.network.MessageBase;
import com.infinityraider.infinitylib.network.serialization.IMessageReader;
import com.infinityraider.infinitylib.network.serialization.IMessageSerializer;
import com.infinityraider.infinitylib.network.serialization.IMessageWriter;
import java.util.List;

import com.infinityraider.infinitylib.network.serialization.PacketBufferUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageTileEntitySeedStorage extends MessageBase {
    private BlockPos pos;
    private int slotId;
    private int amount;
    private IAgriStat stats;

    public MessageTileEntitySeedStorage() {}

    public MessageTileEntitySeedStorage(BlockPos pos, SeedStorageSlot slot) {
        this.pos = pos;
        if (slot != null) {
            this.slotId = slot.getId();
            this.amount = slot.count;
            this.stats = slot.getSeed().getStat();
        } else {
            this.slotId = -1;
        }
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        TileEntity te = AgriCraft.instance.getClientWorld().getTileEntity(this.pos);
        if (te instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
            ItemStack stack = storage.getLockedSeed().map(s -> s.toStack()).orElse(null);
            if (stack != null) {
                stack.setCount(this.amount);
                CompoundNBT tag = new CompoundNBT();
                stats.writeToNBT(tag);
                stack.setTag(tag);
                storage.setSlotContents(this.slotId, stack);
            }
        }
    }

    @Override
    protected List<IMessageSerializer> getNecessarySerializers() {
        return ImmutableList.of(new IMessageSerializer<IAgriStat>() {
            @Override
            public boolean accepts(Class<IAgriStat> clazz) {
                return IAgriStat.class.isAssignableFrom(clazz);
            }

            @Override
            public IMessageWriter<IAgriStat> getWriter(Class<IAgriStat> clazz) {
                return (buf, data) -> {
                    CompoundNBT tag = new CompoundNBT();
                    data.writeToNBT(tag);
                    PacketBufferUtil.writeNBT(buf, tag);
                };
            }

            @Override
            public IMessageReader<IAgriStat> getReader(Class<IAgriStat> clazz) {
                return buf -> AgriApi.getStatRegistry().valueOf(PacketBufferUtil.readNBT(buf)).get();
            }
        });
    }
}
