package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetworkPart;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;

public class CapabilityIrrigationNetworkData implements IInfCapabilityImplementation<Chunk, CapabilityIrrigationNetworkData.Impl> {
    private static final CapabilityIrrigationNetworkData INSTANCE = new CapabilityIrrigationNetworkData();

    public static CapabilityIrrigationNetworkData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_DATA);

    @CapabilityInject(CapabilityIrrigationNetworkData.Impl.class)
    public static final Capability<CapabilityIrrigationNetworkData.Impl> CAPABILITY = null;

    public IrrigationNetworkPart getPart(Chunk chunk, int id) {
        return chunk.getCapability()
    }

    @Override
    public Class<CapabilityIrrigationNetworkData.Impl> getCapabilityClass() {
        return CapabilityIrrigationNetworkData.Impl.class;
    }

    @Override
    public Capability<CapabilityIrrigationNetworkData.Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(Chunk carrier) {
        return true;
    }

    @Override
    public CapabilityIrrigationNetworkData.Impl createNewValue(Chunk chunk) {
        return new CapabilityIrrigationNetworkData.Impl(chunk);
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

        }

        @Override
        public void readFromNBT(CompoundNBT tag) {

        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            return tag;
        }
    }
}