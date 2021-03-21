package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetworkPart;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;

public class CapabilityIrrigationNetworkChunkData implements IInfSerializableCapabilityImplementation<Chunk, CapabilityIrrigationNetworkChunkData.Impl> {
    private static final CapabilityIrrigationNetworkChunkData INSTANCE = new CapabilityIrrigationNetworkChunkData();

    public static CapabilityIrrigationNetworkChunkData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_DATA);

    @CapabilityInject(CapabilityIrrigationNetworkChunkData.Impl.class)
    public static final Capability<CapabilityIrrigationNetworkChunkData.Impl> CAPABILITY = null;

    public boolean registerPart(IrrigationNetworkPart part) {
        if(part.isValid()) {
            return part.getChunk().getCapability(this.getCapability())
                    .map(impl -> {
                        impl.registerPart(part);
                        return true;
                    }).orElse(false);
        }
        return false;
    }

    public IrrigationNetworkPart getPart(Chunk chunk, int id) {
        return chunk.getCapability(this.getCapability())
                .map(impl -> impl.getPart(id)).orElse(IrrigationNetworkPart.createEmpty(chunk));
    }

    @Override
    public Class<CapabilityIrrigationNetworkChunkData.Impl> getCapabilityClass() {
        return CapabilityIrrigationNetworkChunkData.Impl.class;
    }

    @Override
    public Capability<CapabilityIrrigationNetworkChunkData.Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(Chunk carrier) {
        return true;
    }

    @Override
    public CapabilityIrrigationNetworkChunkData.Impl createNewValue(Chunk chunk) {
        return new CapabilityIrrigationNetworkChunkData.Impl(chunk);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<Chunk> getCarrierClass() {
        return Chunk.class;
    }

    public static class Impl implements ISerializable {
        private final Chunk chunk;

        private final Map<Integer, IrrigationNetworkPart> parts;

        private Impl(Chunk chunk) {
            this.chunk = chunk;
            this.parts = Maps.newHashMap();
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        public IrrigationNetworkPart getPart(int id) {
            return this.parts.get(id);
        }

        private void registerPart(IrrigationNetworkPart part) {
            this.parts.put(part.getId(), part);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            // TODO
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            // TODO
            return tag;
        }
    }
}
