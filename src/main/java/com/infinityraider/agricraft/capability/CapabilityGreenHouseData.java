package com.infinityraider.agricraft.capability;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.GreenHouse;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;
import java.util.Set;

public class CapabilityGreenHouseData implements IInfSerializableCapabilityImplementation<Chunk, CapabilityGreenHouseData.Impl> {
    private static final CapabilityGreenHouseData INSTANCE = new CapabilityGreenHouseData();

    public static CapabilityGreenHouseData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GREENHOUSE);

    @CapabilityInject(CapabilityGreenHouseData.Impl.class)
    public static final Capability<CapabilityGreenHouseData.Impl> CAPABILITY = null;

    private CapabilityGreenHouseData() {}

    public void addGreenHouse(World world, GreenHouse greenHouse) {
        greenHouse.getChunks().stream()
                .map((pos) -> world.getChunk(pos.x, pos.z))
                .map(this::getCapability)
                .map(LazyOptional::resolve)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(impl -> impl.add(greenHouse));
    }

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

        private final Set<GreenHouse.Part> greenhouses;

        protected Impl(Chunk chunk) {
            this.chunk = chunk;
            this.greenhouses = Sets.newIdentityHashSet();
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        public ChunkPos getChunkPos() {
            return this.getChunk().getPos();
        }

        protected void add(GreenHouse greenHouse) {
            this.greenhouses.add(greenHouse.getPart(this.getChunkPos()));
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            // TODO
        }

        @Override
        public CompoundNBT writeToNBT() {
            ListNBT entries = new ListNBT();
            this.greenhouses.stream()
                    .map(GreenHouse.Part::writeToTag)
                    .forEach(entries::add);
            CompoundNBT tag = new CompoundNBT();
            tag.put(AgriNBT.ENTRIES, entries);
            return tag;
        }
    }
}
