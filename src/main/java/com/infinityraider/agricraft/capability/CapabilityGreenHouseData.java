package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityGreenHouseData implements IInfSerializableCapabilityImplementation<Chunk, CapabilityGreenHouseData.Impl> {
    private static final CapabilityGreenHouseData INSTANCE = new CapabilityGreenHouseData();

    public static CapabilityGreenHouseData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE);

    @CapabilityInject(CapabilityGreenHouseData.Impl.class)
    public static final Capability<CapabilityGreenHouseData.Impl> CAPABILITY = null;

    private CapabilityGreenHouseData() {}

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(Chunk carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(Chunk carrier) {
        return new Impl(carrier);
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



        protected Impl(Chunk chunk) {
            this.chunk = chunk;
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {

        }

        @Override
        public CompoundNBT writeToNBT() {
            return null;
        }
    }
}
